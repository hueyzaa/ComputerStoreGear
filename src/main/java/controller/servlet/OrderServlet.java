package controller.servlet;

import dao.OrderDAO;
import dao.ShoppingCartDAO;
import dao.UserAddressDAO;
import dao.CouponDAO;
import dao.UserCouponUsageDAO;
import model.Order;
import model.ShoppingCart;
import model.User;
import model.UserAddress;
import model.Coupon;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/checkout", "/checkout/place-order", "/orders", "/orders/detail"})
public class OrderServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OrderServlet.class.getName());
    private OrderDAO orderDAO;
    private ShoppingCartDAO shoppingCartDAO;
    private UserAddressDAO userAddressDAO;
    private CouponDAO couponDAO;
    private UserCouponUsageDAO userCouponUsageDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
        shoppingCartDAO = new ShoppingCartDAO();
        userAddressDAO = new UserAddressDAO();
        couponDAO = new CouponDAO();
        userCouponUsageDAO = new UserCouponUsageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        switch (path) {
            case "/checkout":
                displayCheckout(request, response, currentUser.getUserID());
                break;
            case "/orders":
                displayUserOrders(request, response, currentUser.getUserID());
                break;
            case "/orders/detail":
                displayOrderDetail(request, response, currentUser.getUserID());
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("applyCoupon".equals(action)) {
            handleApplyCoupon(request, response, currentUser.getUserID());
        } else if ("removeCoupon".equals(action)) {
            handleRemoveCoupon(request, response, currentUser.getUserID());
        } else {
            switch (path) {
                case "/checkout/place-order":
                    handlePlaceOrder(request, response, currentUser.getUserID());
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        }
    }

    private void displayCheckout(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        List<ShoppingCart> cartItems = shoppingCartDAO.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            request.setAttribute("errorMessage", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm để thanh toán.");
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
            return;
        }

        List<UserAddress> addresses = userAddressDAO.getAddressesByUserId(userId);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ShoppingCart item : cartItems) {
            if (item.getProduct() != null) {
                subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = (String) request.getSession().getAttribute("appliedCouponCode");
        if (appliedCouponCode != null && !appliedCouponCode.isEmpty()) {
            Coupon coupon = couponDAO.getCouponByCode(appliedCouponCode);
            if (coupon != null) {
                discountAmount = calculateDiscount(subtotal, coupon);
                request.setAttribute("appliedCouponCode", appliedCouponCode);
                request.setAttribute("discountAmount", discountAmount);
            } else {
                // Mã giảm giá không còn hợp lệ, xóa khỏi session
                request.getSession().removeAttribute("appliedCouponCode");
                request.getSession().removeAttribute("discountAmount");
            }
        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("addresses", addresses);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("totalAmount", totalAmount);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    private void handleApplyCoupon(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String couponCode = request.getParameter("couponCode");
        if (couponCode == null || couponCode.trim().isEmpty()) {
            request.setAttribute("couponMessage", "Vui lòng nhập mã giảm giá.");
            request.setAttribute("couponSuccess", false);
            displayCheckout(request, response, userId);
            return;
        }

        Coupon coupon = couponDAO.getCouponByCode(couponCode.trim());
        if (coupon == null) {
            request.setAttribute("couponMessage", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
            request.setAttribute("couponSuccess", false);
            displayCheckout(request, response, userId);
            return;
        }

        List<ShoppingCart> cartItems = shoppingCartDAO.getCartItemsByUserId(userId);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ShoppingCart item : cartItems) {
            if (item.getProduct() != null) {
                subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            request.setAttribute("couponMessage", "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá này (" + coupon.getMinOrderAmount() + ").");
            request.setAttribute("couponSuccess", false);
            displayCheckout(request, response, userId);
            return;
        }

        // Kiểm tra giới hạn sử dụng
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            request.setAttribute("couponMessage", "Mã giảm giá này đã hết lượt sử dụng.");
            request.setAttribute("couponSuccess", false);
            displayCheckout(request, response, userId);
            return;
        }

        request.getSession().setAttribute("appliedCouponCode", coupon.getCouponCode());
        request.getSession().setAttribute("discountAmount", calculateDiscount(subtotal, coupon));
        request.setAttribute("couponMessage", "Mã giảm giá đã được áp dụng thành công!");
        request.setAttribute("couponSuccess", true);
        displayCheckout(request, response, userId);
    }

    private void handleRemoveCoupon(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        request.getSession().removeAttribute("appliedCouponCode");
        request.getSession().removeAttribute("discountAmount");
        request.setAttribute("couponMessage", "Mã giảm giá đã được xóa.");
        request.setAttribute("couponSuccess", true);
        displayCheckout(request, response, userId);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, Coupon coupon) {
        BigDecimal discount = BigDecimal.ZERO;
        if ("percentage".equals(coupon.getDiscountType())) {
            discount = subtotal.multiply(coupon.getDiscountValue().divide(new BigDecimal(100)));
        } else if ("fixed_amount".equals(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }

        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }
        return discount;
    }

    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String shippingAddressIdParam = request.getParameter("shippingAddressId");
        String billingAddressIdParam = request.getParameter("billingAddressId");
        String paymentMethod = request.getParameter("paymentMethod");
        String notes = request.getParameter("notes");
        String appliedCouponCode = (String) request.getSession().getAttribute("appliedCouponCode");
        BigDecimal discountAmount = (BigDecimal) request.getSession().getAttribute("discountAmount");

        if (shippingAddressIdParam == null || shippingAddressIdParam.trim().isEmpty() ||
            billingAddressIdParam == null || billingAddressIdParam.trim().isEmpty() ||
            paymentMethod == null || paymentMethod.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn địa chỉ giao hàng, địa chỉ thanh toán và phương thức thanh toán.");
            displayCheckout(request, response, userId); // Quay lại trang checkout với lỗi
            return;
        }

        try {
            UUID shippingAddressId = UUID.fromString(shippingAddressIdParam);
            UUID billingAddressId = UUID.fromString(billingAddressIdParam);

            // Kiểm tra xem địa chỉ có thuộc về người dùng không
            UserAddress shippingAddress = userAddressDAO.getAddressById(shippingAddressId);
            UserAddress billingAddress = userAddressDAO.getAddressById(billingAddressId);

            if (shippingAddress == null || !shippingAddress.getUserID().equals(userId) ||
                billingAddress == null || !billingAddress.getUserID().equals(userId)) {
                request.setAttribute("errorMessage", "Địa chỉ không hợp lệ hoặc không thuộc về tài khoản của bạn.");
                displayCheckout(request, response, userId);
                return;
            }

            // Lấy tổng số tiền từ giỏ hàng (sau khi áp dụng giảm giá nếu có)
            List<ShoppingCart> cartItems = shoppingCartDAO.getCartItemsByUserId(userId);
            BigDecimal subtotal = BigDecimal.ZERO;
            for (ShoppingCart item : cartItems) {
                if (item.getProduct() != null) {
                    subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
            BigDecimal finalTotalAmount = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);

            // Khai báo newOrder ở đây
            Order newOrder = null;

            LOGGER.info("Calling sendPaymentRequest for user: " + userId);
            String paymentResult = sendPaymentRequest(UUID.randomUUID().toString(), finalTotalAmount, paymentMethod, request.getParameter("cardNumber"), request.getParameter("expiryDate"), request.getParameter("cvv"));
            LOGGER.info("Payment request returned with result: " + paymentResult + " for user: " + userId);

            if ("SUCCESS".equals(paymentResult)) {
                LOGGER.info("Payment successful. Proceeding to create order for user: " + userId);
                newOrder = orderDAO.createOrder(userId, shippingAddressId, billingAddressId, paymentMethod, notes);

                if (newOrder != null) {
                    // Ghi lại việc sử dụng mã giảm giá nếu có
                    if (appliedCouponCode != null && !appliedCouponCode.isEmpty()) {
                        Coupon coupon = couponDAO.getCouponByCode(appliedCouponCode);
                        if (coupon != null) {
                            userCouponUsageDAO.addCouponUsage(userId, coupon.getCouponID(), newOrder.getOrderID());
                            couponDAO.incrementCouponUsedCount(coupon.getCouponID());
                        }
                    }
                    // Xóa mã giảm giá khỏi session sau khi đặt hàng
                    request.getSession().removeAttribute("appliedCouponCode");
                    request.getSession().removeAttribute("discountAmount");

                    // Xóa giỏ hàng sau khi đặt hàng thành công
                    shoppingCartDAO.clearCart(userId);

                    LOGGER.info("Order placed successfully: " + newOrder.getOrderID());
                    response.sendRedirect(request.getContextPath() + "/orders/detail?id=" + newOrder.getOrderID() + "&message=" + URLEncoder.encode("Đơn hàng của bạn đã được đặt thành công!", StandardCharsets.UTF_8.toString()));
                } else {
                    LOGGER.severe("Order creation returned null for user: " + userId);
                    request.setAttribute("errorMessage", "Không thể đặt đơn hàng. Vui lòng kiểm tra lại giỏ hàng và số lượng sản phẩm hoặc liên hệ hỗ trợ.");
                    request.getRequestDispatcher("/error.jsp").forward(request, response); // Redirect to generic error page
                }
            } else {
                LOGGER.warning("Payment failed for user: " + userId + ". Payment result: " + paymentResult);
                request.setAttribute("errorMessage", "Thanh toán thất bại. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.");
                displayCheckout(request, response, userId);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid UUID format for address IDs: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID địa chỉ không hợp lệ.");
            displayCheckout(request, response, userId);
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during order placement for user " + userId + ": " + e.getMessage());
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn khi đặt hàng. Vui lòng thử lại.");
            request.getRequestDispatcher("/error.jsp").forward(request, response); // Redirect to generic error page
        }
    }

    private String sendPaymentRequest(String orderId, BigDecimal totalAmount, String paymentMethod, String cardNumber, String expiryDate, String cvv) throws IOException {
        LOGGER.info("Initiating payment request for orderId: " + orderId);
        // In a real application, you would use an HTTP client to send a request to the payment gateway.
        // For this simulation, we'll directly call a static method on the PaymentGatewayServlet.
        boolean paymentSuccess = PaymentGatewayServlet.simulatePaymentProcessing();

        if (paymentSuccess) {
            LOGGER.info("Payment successful for orderId: " + orderId);
            return "SUCCESS";
        } else {
            LOGGER.warning("Payment failed for orderId: " + orderId);
            return "FAILED";
        }
    }

    private void displayUserOrders(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/orders.jsp").forward(request, response);
    }

    private void displayOrderDetail(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String orderIdParam = request.getParameter("id");
        LOGGER.info("Attempting to display order detail for orderId: " + orderIdParam + " for user: " + userId);

        if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID đơn hàng không được cung cấp.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            LOGGER.warning("Order ID not provided for displayOrderDetail.");
            return;
        }

        try {
            UUID orderId = UUID.fromString(orderIdParam);
            LOGGER.info("Parsed orderId UUID: " + orderId);
            Order order = orderDAO.getOrderById(orderId);
            LOGGER.info("Retrieved order from DAO. Order is null: " + (order == null));

            if (order == null || !order.getUserID().equals(userId)) {
                request.setAttribute("errorMessage", "Đơn hàng không tìm thấy hoặc bạn không có quyền truy cập.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                LOGGER.warning("Order not found or unauthorized access for orderId: " + orderId + " by user: " + userId);
                return;
            }

            if (order.getOrderDate() != null) {
                request.setAttribute("orderDate", java.util.Date.from(order.getOrderDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
                LOGGER.info("Converted orderDate to java.util.Date and set attribute.");
            }

            request.setAttribute("order", order);
            LOGGER.info("Set 'order' attribute in request. Forwarding to order-detail.jsp.");
            request.getRequestDispatcher("/order-detail.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid order ID format: " + orderIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID đơn hàng không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.severe("Unexpected error in displayOrderDetail for orderId " + orderIdParam + ": " + e.getMessage());
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn khi hiển thị chi tiết đơn hàng.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
