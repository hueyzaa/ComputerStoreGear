package dao;

import model.ShoppingCart;
import model.Product;
import model.User;
import util.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoppingCartDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartDAO.class);
    private final ProductDAO productDAO = new ProductDAO(); // Để lấy thông tin sản phẩm
    private final AuthDAO authDAO; // Để lấy thông tin người dùng

    public ShoppingCartDAO() {
        // AuthDAO cần EmailService và FileService, nhưng ở đây chúng ta chỉ cần nó để lấy User
        // Nếu AuthDAO không có constructor mặc định, bạn cần truyền các dependency của nó vào đây
        // Tạm thời, tôi sẽ tạo một AuthDAO đơn giản nếu không có EmailService/FileService
        // Trong một ứng dụng thực tế, bạn nên sử dụng Dependency Injection framework
        this.authDAO = new AuthDAO(null); // Pass null for EmailService for now, as it's not used in getUserById
    }

    /**
     * Thêm sản phẩm vào giỏ hàng hoặc cập nhật số lượng nếu đã tồn tại.
     * @param userId ID của người dùng.
     * @param productId ID của sản phẩm.
     * @param quantity Số lượng sản phẩm muốn thêm/cập nhật.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean addOrUpdateCartItem(UUID userId, UUID productId, int quantity) {
        String selectSql = "SELECT CartID, Quantity FROM ShoppingCarts WHERE UserID = ? AND ProductID = ?";
        String insertSql = "INSERT INTO ShoppingCarts (CartID, UserID, ProductID, Quantity, AddedDate, ModifiedDate) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE ShoppingCarts SET Quantity = ?, ModifiedDate = ? WHERE CartID = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, userId.toString());
            selectStmt.setString(2, productId.toString());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    // Sản phẩm đã có trong giỏ, cập nhật số lượng
                    UUID cartId = UUID.fromString(rs.getString("CartID"));
                    int currentQuantity = rs.getInt("Quantity");
                    int newQuantity = currentQuantity + quantity;
                    if (newQuantity <= 0) {
                        return removeCartItem(userId, productId); // Xóa nếu số lượng <= 0
                    }

                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                        updateStmt.setString(3, cartId.toString());
                        int rowsAffected = updateStmt.executeUpdate();
                        LOGGER.info("Updated cart item for user {} product {}. New quantity: {}", userId, productId, newQuantity);
                        return rowsAffected > 0;
                    }
                } else {
                    // Sản phẩm chưa có trong giỏ, thêm mới
                    if (quantity <= 0) return false; // Không thêm nếu số lượng <= 0

                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        UUID newCartId = UUID.randomUUID();
                        insertStmt.setString(1, newCartId.toString());
                        insertStmt.setString(2, userId.toString());
                        insertStmt.setString(3, productId.toString());
                        insertStmt.setInt(4, quantity);
                        insertStmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                        insertStmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                        int rowsAffected = insertStmt.executeUpdate();
                        LOGGER.info("Added new cart item for user {} product {}. Quantity: {}", userId, productId, quantity);
                        return rowsAffected > 0;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding/updating cart item for user {} product {}: {}", userId, productId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy tất cả các mặt hàng trong giỏ hàng của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách các đối tượng ShoppingCart.
     */
    public List<ShoppingCart> getCartItemsByUserId(UUID userId) {
        List<ShoppingCart> cartItems = new ArrayList<>();
        String sql = "SELECT CartID, UserID, ProductID, Quantity, AddedDate, ModifiedDate FROM ShoppingCarts WHERE UserID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ShoppingCart item = mapResultSetToShoppingCart(rs);
                    // Lấy thông tin chi tiết sản phẩm và người dùng
                    item.setProduct(productDAO.getProductById(item.getProductID()));
                    // item.setUser(authDAO.getUserById(item.getUserID())); // Cần phương thức getUserById trong AuthDAO
                    cartItems.add(item);
                }
            }
            LOGGER.info("Retrieved {} cart items for user {}. ", cartItems.size(), userId);
        } catch (SQLException e) {
            LOGGER.error("Error retrieving cart items for user {}: {}", userId, e.getMessage(), e);
        }
        return cartItems;
    }

    /**
     * Cập nhật số lượng của một mặt hàng trong giỏ hàng.
     * @param userId ID của người dùng.
     * @param productId ID của sản phẩm.
     * @param newQuantity Số lượng mới.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean updateCartItemQuantity(UUID userId, UUID productId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeCartItem(userId, productId);
        }
        String sql = "UPDATE ShoppingCarts SET Quantity = ?, ModifiedDate = ? WHERE UserID = ? AND ProductID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, userId.toString());
            stmt.setString(4, productId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated quantity for user {} product {}. New quantity: {}", userId, productId, newQuantity);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating cart item quantity for user {} product {}: {}", userId, productId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Xóa một mặt hàng khỏi giỏ hàng.
     * @param userId ID của người dùng.
     * @param productId ID của sản phẩm.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean removeCartItem(UUID userId, UUID productId) {
        String sql = "DELETE FROM ShoppingCarts WHERE UserID = ? AND ProductID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            stmt.setString(2, productId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Removed cart item for user {} product {}. Rows affected: {}", userId, productId, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error removing cart item for user {} product {}: {}", userId, productId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Xóa tất cả các mặt hàng khỏi giỏ hàng của một người dùng.
     * @param userId ID của người dùng.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean clearCart(UUID userId) {
        String sql = "DELETE FROM ShoppingCarts WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Cleared cart for user {}. Rows affected: {}", userId, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error clearing cart for user {}: {}", userId, e.getMessage(), e);
        }
        return false;
    }

    private ShoppingCart mapResultSetToShoppingCart(ResultSet rs) throws SQLException {
        ShoppingCart item = new ShoppingCart();
        item.setCartID(UUID.fromString(rs.getString("CartID")));
        item.setUserID(UUID.fromString(rs.getString("UserID")));
        item.setProductID(UUID.fromString(rs.getString("ProductID")));
        item.setQuantity(rs.getInt("Quantity"));
        if (rs.getTimestamp("AddedDate") != null) {
            item.setAddedDate(rs.getTimestamp("AddedDate").toLocalDateTime());
        }
        if (rs.getTimestamp("ModifiedDate") != null) {
            item.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
        }
        return item;
    }
}
