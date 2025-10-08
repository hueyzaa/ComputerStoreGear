package dao;

import model.*;
import util.DBContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAO.class);
    private final ProductDAO productDAO = new ProductDAO();
    private final ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO();
    private final UserAddressDAO userAddressDAO = new UserAddressDAO();
    private final InventoryTransactionDAO inventoryTransactionDAO = new InventoryTransactionDAO();

    /**
     * Tạo một đơn hàng mới từ các mặt hàng trong giỏ hàng của người dùng.
     * Phương thức này là transactional.
     * @param userId ID của người dùng đặt hàng.
     * @param shippingAddressId ID của địa chỉ giao hàng.
     * @param billingAddressId ID của địa chỉ thanh toán.
     * @param paymentMethod Phương thức thanh toán.
     * @param notes Ghi chú cho đơn hàng.
     * @return Đối tượng Order đã tạo nếu thành công, ngược lại null.
     */
    public Order createOrder(UUID userId, UUID shippingAddressId, UUID billingAddressId, String paymentMethod, String notes) {
        Order order = null;
        try {
            connection.setAutoCommit(false); // Bắt đầu transaction
            LOGGER.info("OrderDAO: Transaction started for user {}.", userId);

            List<ShoppingCart> cartItems = shoppingCartDAO.getCartItemsByUserId(userId);
            if (cartItems.isEmpty()) {
                LOGGER.warn("OrderDAO: Cannot create order: Cart is empty for user {}.", userId);
                connection.rollback();
                return null;
            }

            // Tính toán tổng tiền
            BigDecimal subtotalAmount = BigDecimal.ZERO;
            for (ShoppingCart item : cartItems) {
                Product product = productDAO.getProductById(item.getProductID());
                if (product == null || !product.isActive() || product.getStockQuantity() < item.getQuantity()) {
                    LOGGER.warn("Cannot create order: Product {} is out of stock or inactive.", item.getProductID());
                    connection.rollback();
                    return null;
                }
                subtotalAmount = subtotalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            LOGGER.info("OrderDAO: Calculated subtotal amount {} for user {}.", subtotalAmount, userId);

            // Tạo Order mới
            UUID orderId = UUID.randomUUID();
            String orderNumber = generateOrderNumber(); // Tự tạo mã đơn hàng
            String insertOrderSql = "INSERT INTO Orders (OrderID, OrderNumber, UserID, OrderStatus, PaymentStatus, PaymentMethod, " +
                    "SubtotalAmount, TaxAmount, ShippingAmount, DiscountAmount, TotalAmount, CurrencyCode, " +
                    "ShippingAddressID, BillingAddressID, Notes, OrderDate, ModifiedDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(insertOrderSql)) {
                stmt.setString(1, orderId.toString());
                stmt.setString(2, orderNumber);
                stmt.setString(3, userId.toString());
                stmt.setString(4, "Pending"); // Trạng thái đơn hàng ban đầu
                stmt.setString(5, "Pending"); // Trạng thái thanh toán ban đầu
                stmt.setString(6, paymentMethod);
                stmt.setBigDecimal(7, subtotalAmount);
                stmt.setBigDecimal(8, BigDecimal.ZERO); // Tạm thời 0
                stmt.setBigDecimal(9, BigDecimal.ZERO); // Tạm thời 0
                stmt.setBigDecimal(10, BigDecimal.ZERO); // Tạm thời 0
                stmt.setBigDecimal(11, subtotalAmount); // Total = Subtotal (tạm thời)
                stmt.setString(12, "VND");
                stmt.setString(13, shippingAddressId.toString());
                stmt.setString(14, billingAddressId.toString());
                stmt.setString(15, notes);
                stmt.setTimestamp(16, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                stmt.setTimestamp(17, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();
            }
            LOGGER.info("OrderDAO: Order record inserted for order ID {}.", orderId);

            // Thêm OrderItems và cập nhật số lượng tồn kho
            String insertOrderItemSql = "INSERT INTO OrderItems (OrderItemID, OrderID, ProductID, ProductName, SKU, Quantity, UnitPrice, TotalPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String updateProductStockSql = "UPDATE Products SET StockQuantity = StockQuantity - ?, ModifiedDate = ? WHERE ProductID = ?";

            for (ShoppingCart item : cartItems) {
                Product product = productDAO.getProductById(item.getProductID());
                UUID orderItemId = UUID.randomUUID();
                LOGGER.info("OrderDAO: Processing cart item for product ID {} (quantity {}).", item.getProductID(), item.getQuantity());
                try (PreparedStatement itemStmt = connection.prepareStatement(insertOrderItemSql)) {
                    itemStmt.setString(1, orderItemId.toString());
                    itemStmt.setString(2, orderId.toString());
                    itemStmt.setString(3, item.getProductID().toString());
                    itemStmt.setString(4, product.getProductName());
                    itemStmt.setString(5, product.getSku());
                    itemStmt.setInt(6, item.getQuantity());
                    itemStmt.setBigDecimal(7, product.getPrice());
                    itemStmt.setBigDecimal(8, product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    itemStmt.executeUpdate();
                }
                LOGGER.info("OrderDAO: Order item inserted for product ID {}.", item.getProductID());

                try (PreparedStatement stockStmt = connection.prepareStatement(updateProductStockSql)) {
                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    stockStmt.setString(3, item.getProductID().toString());
                    stockStmt.executeUpdate();
                }
                LOGGER.info("OrderDAO: Product stock updated for product ID {}.", item.getProductID());

                // Ghi lại giao dịch tồn kho (loại 'out')
                InventoryTransaction invTransaction = new InventoryTransaction();
                invTransaction.setProductID(item.getProductID());
                invTransaction.setTransactionType("out");
                invTransaction.setQuantity(item.getQuantity());
                invTransaction.setReferenceType("order");
                invTransaction.setReferenceID(orderId);
                invTransaction.setNotes("Order placed: " + orderNumber);
                invTransaction.setCreatedBy(userId); // Người dùng đặt hàng là người tạo giao dịch
                inventoryTransactionDAO.addTransaction(invTransaction, connection);
                LOGGER.info("OrderDAO: Inventory transaction added for product ID {}.", item.getProductID());
            }

            // Xóa giỏ hàng sau khi tạo đơn hàng thành công
            shoppingCartDAO.clearCart(userId);
            LOGGER.info("OrderDAO: Shopping cart cleared for user {}.", userId);

            connection.commit(); // Commit transaction
            LOGGER.info("OrderDAO: Transaction committed for order {}.", orderId);

            // Lấy lại đối tượng Order đầy đủ để trả về
            order = getOrderById(orderId);

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback nếu có lỗi
            } catch (SQLException rollbackEx) {
                LOGGER.error("Error during rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            LOGGER.error("Error creating order for user {}: {}", userId, e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true); // Trả lại chế độ auto-commit
            } catch (SQLException e) {
                LOGGER.error("Error resetting auto-commit: {}", e.getMessage(), e);
            }
        }
        return order;
    }

    /**
     * Lấy một đơn hàng cụ thể bằng ID.
     * @param orderId ID của đơn hàng.
     * @return Đối tượng Order nếu tìm thấy, ngược lại null.
     */
    public Order getOrderById(UUID orderId) {
        Order order = null;
        String sql = "SELECT OrderID, OrderNumber, UserID, OrderStatus, PaymentStatus, PaymentMethod, PaymentTransactionID, " +
                "SubtotalAmount, TaxAmount, ShippingAmount, DiscountAmount, TotalAmount, CurrencyCode, " +
                "ShippingAddressID, BillingAddressID, ShippingTrackingNumber, ShippingCarrier, Notes, " +
                "OrderDate, ShippedDate, DeliveredDate, ModifiedDate FROM Orders WHERE OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, orderId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    order = mapResultSetToOrder(rs);
                    // Lấy OrderItems
                    List<OrderItem> orderItems = getOrderItemsByOrderId(order.getOrderID());
                    order.setOrderItems(orderItems);
                    LOGGER.info("Retrieved {} order items for order ID {}.", orderItems.size(), order.getOrderID());

                    // Lấy địa chỉ giao hàng và thanh toán
                    UserAddress shippingAddress = userAddressDAO.getAddressById(order.getShippingAddressID());
                    order.setShippingAddress(shippingAddress);
                    LOGGER.info("Retrieved shipping address for order ID {}: {}.", order.getOrderID(), shippingAddress != null ? shippingAddress.getAddressID() : "null");

                    UserAddress billingAddress = userAddressDAO.getAddressById(order.getBillingAddressID());
                    order.setBillingAddress(billingAddress);
                    LOGGER.info("Retrieved billing address for order ID {}: {}.", order.getOrderID(), billingAddress != null ? billingAddress.getAddressID() : "null");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting order by ID {}: {}", orderId, e.getMessage(), e);
        }
        return order;
    }

    /**
     * Lấy tất cả các đơn hàng của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách các đối tượng Order.
     */
    public List<Order> getOrdersByUserId(UUID userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT OrderID, OrderNumber, UserID, OrderStatus, PaymentStatus, PaymentMethod, PaymentTransactionID, " +
                "SubtotalAmount, TaxAmount, ShippingAmount, DiscountAmount, TotalAmount, CurrencyCode, " +
                "ShippingAddressID, BillingAddressID, ShippingTrackingNumber, ShippingCarrier, Notes, " +
                "OrderDate, ShippedDate, DeliveredDate, ModifiedDate FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setOrderItems(getOrderItemsByOrderId(order.getOrderID()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting orders by user ID {}: {}", userId, e.getMessage(), e);
        }
        return orders;
    }

    /**
     * Lấy tất cả các đơn hàng.
     * @return Danh sách các đối tượng Order.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.OrderNumber, o.UserID, o.OrderStatus, o.PaymentStatus, o.PaymentMethod, o.PaymentTransactionID, " +
                "o.SubtotalAmount, o.TaxAmount, o.ShippingAmount, o.DiscountAmount, o.TotalAmount, o.CurrencyCode, " +
                "o.ShippingAddressID, o.BillingAddressID, o.ShippingTrackingNumber, o.ShippingCarrier, o.Notes, " +
                "o.OrderDate, o.ShippedDate, o.DeliveredDate, o.ModifiedDate, " +
                "u.Username, u.Email " +
                "FROM Orders o JOIN Users u ON o.UserID = u.UserID ORDER BY o.OrderDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                // Set user information
                User user = new User();
                user.setUserID(UUID.fromString(rs.getString("UserID")));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                order.setUser(user);

                order.setOrderItems(getOrderItemsByOrderId(order.getOrderID()));
                orders.add(order);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting all orders: {}", e.getMessage(), e);
        }
        return orders;
    }

    /**
     * Lấy tổng số đơn hàng.
     * @return Tổng số đơn hàng.
     */
    public int getTotalOrderCount() {
        String sql = "SELECT COUNT(*) FROM Orders";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting total order count: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Lấy tổng số đơn hàng có trạng thái 'Pending'.
     * @return Tổng số đơn hàng 'Pending'.
     */
    public int getTotalPendingOrderCount() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE OrderStatus = 'Pending'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting total pending order count: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Lấy tổng số đơn hàng có trạng thái 'Processing'.
     * @return Tổng số đơn hàng 'Processing'.
     */
    public int getTotalProcessingOrderCount() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE OrderStatus = 'Processing'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting total processing order count: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng.
     * @param orderId ID của đơn hàng cần cập nhật.
     * @param newStatus Trạng thái mới của đơn hàng (ví dụ: "Pending", "Processing", "Shipped", "Delivered", "Cancelled").
     * @return true nếu cập nhật thành công, ngược lại false.
     */
    public boolean updateOrderStatus(UUID orderId, String newStatus) {
        String sql = "UPDATE Orders SET OrderStatus = ?, ModifiedDate = ?";
        if ("Shipped".equalsIgnoreCase(newStatus)) {
            sql += ", ShippedDate = ?";
        } else if ("Delivered".equalsIgnoreCase(newStatus)) {
            sql += ", DeliveredDate = ?, PaymentStatus = ?";
        }
        sql += " WHERE OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            int paramIndex = 3;
            if ("Shipped".equalsIgnoreCase(newStatus)) {
                stmt.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            }
            else if ("Delivered".equalsIgnoreCase(newStatus)) {
                stmt.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(paramIndex++, "Paid"); // Set payment status to Paid when delivered
            }
            stmt.setString(paramIndex, orderId.toString());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Order ID {} status updated to {}.", orderId, newStatus);
                return true;
            } else {
                LOGGER.warn("Order ID {} not found for status update.", orderId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating order status for ID {}: {}", orderId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cập nhật trạng thái thanh toán của một đơn hàng.
     * @param orderId ID của đơn hàng cần cập nhật.
     * @param newPaymentStatus Trạng thái thanh toán mới (ví dụ: "Pending", "Paid", "Refunded").
     * @return true nếu cập nhật thành công, ngược lại false.
     */
    public boolean updatePaymentStatus(UUID orderId, String newPaymentStatus) {
        String sql = "UPDATE Orders SET PaymentStatus = ?, ModifiedDate = ? WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPaymentStatus);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, orderId.toString());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Order ID {} payment status updated to {}.", orderId, newPaymentStatus);
                return true;
            } else {
                LOGGER.warn("Order ID {} not found for payment status update.", orderId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating order payment status for ID {}: {}", orderId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy tất cả các mặt hàng trong một đơn hàng cụ thể.
     * @param orderId ID của đơn hàng.
     * @return Danh sách các đối tượng OrderItem.
     */
    private List<OrderItem> getOrderItemsByOrderId(UUID orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT OrderItemID, OrderID, ProductID, ProductName, SKU, Quantity, UnitPrice, TotalPrice FROM OrderItems WHERE OrderID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, orderId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemID(UUID.fromString(rs.getString("OrderItemID")));
                    item.setOrderID(UUID.fromString(rs.getString("OrderID")));
                    item.setProductID(UUID.fromString(rs.getString("ProductID")));
                    item.setProductName(rs.getString("ProductName"));
                    item.setSku(rs.getString("SKU"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    item.setTotalPrice(rs.getBigDecimal("TotalPrice"));
                    orderItems.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting order items for order ID {}: {}", orderId, e.getMessage(), e);
        }
        return orderItems;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(UUID.fromString(rs.getString("OrderID")));
        order.setOrderNumber(rs.getString("OrderNumber"));
        order.setUserID(UUID.fromString(rs.getString("UserID")));
        order.setOrderStatus(rs.getString("OrderStatus"));
        order.setPaymentStatus(rs.getString("PaymentStatus"));
        order.setPaymentMethod(rs.getString("PaymentMethod"));
        order.setPaymentTransactionID(rs.getString("PaymentTransactionID"));
        order.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        order.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        order.setShippingAmount(rs.getBigDecimal("ShippingAmount"));
        order.setDiscountAmount(rs.getBigDecimal("DiscountAmount"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setCurrencyCode(rs.getString("CurrencyCode"));
        order.setShippingAddressID(UUID.fromString(rs.getString("ShippingAddressID")));
        order.setBillingAddressID(UUID.fromString(rs.getString("BillingAddressID")));
        order.setShippingTrackingNumber(rs.getString("ShippingTrackingNumber"));
        order.setShippingCarrier(rs.getString("ShippingCarrier"));
        order.setNotes(rs.getString("Notes"));
        if (rs.getTimestamp("OrderDate") != null) {
            order.setOrderDate(rs.getTimestamp("OrderDate").toLocalDateTime());
        } else {
            order.setOrderDate(null);
        }
        if (rs.getTimestamp("ShippedDate") != null) {
            order.setShippedDate(rs.getTimestamp("ShippedDate").toLocalDateTime());
        }
        if (rs.getTimestamp("DeliveredDate") != null) {
            order.setDeliveredDate(rs.getTimestamp("DeliveredDate").toLocalDateTime());
        }
        if (rs.getTimestamp("ModifiedDate") != null) {
            order.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
        }
        return order;
    }

    private String generateOrderNumber() {
        // Logic để tạo mã đơn hàng duy nhất (ví dụ: dựa trên thời gian, UUID rút gọn, hoặc sequence)
        // Tạm thời dùng UUID rút gọn
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}