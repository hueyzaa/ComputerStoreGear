package controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/payment-gateway"})
public class PaymentGatewayServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PaymentGatewayServlet.class.getName());
    private final Random random = new Random();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy thông tin từ request
        String orderId = request.getParameter("orderId");
        String totalAmount = request.getParameter("totalAmount");
        String paymentMethod = request.getParameter("paymentMethod");
        String cardNumber = request.getParameter("cardNumber"); // Chỉ để mô phỏng
        String expiryDate = request.getParameter("expiryDate"); // Chỉ để mô phỏng
        String cvv = request.getParameter("cvv"); // Chỉ để mô phỏng

        LOGGER.info("Received payment request for Order ID: " + orderId + ", Amount: " + totalAmount + ", Method: " + paymentMethod);

        // Mô phỏng quá trình xử lý thanh toán
        // Trong thực tế, đây sẽ là nơi gọi API của cổng thanh toán thật
        boolean paymentSuccess = simulatePaymentProcessing();

        // Trả về kết quả thanh toán
        if (paymentSuccess) {
            LOGGER.info("Payment successful for Order ID: " + orderId);
            response.getWriter().write("SUCCESS");
        } else {
            LOGGER.warning("Payment failed for Order ID: " + orderId);
            response.getWriter().write("FAILED");
        }
    }

    public static boolean simulatePaymentProcessing() {
        // Mô phỏng thành công 80% các giao dịch
        return true;
    }
}
