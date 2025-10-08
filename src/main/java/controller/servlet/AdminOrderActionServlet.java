package controller.servlet;

import dao.OrderDAO;
import util.DBContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminOrderActionServlet", urlPatterns = {"/admin/order/updateStatus", "/admin/order/updatePaymentStatus"})
public class AdminOrderActionServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminOrderActionServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdParam = request.getParameter("orderId");
        String action = request.getParameter("action"); // New: Get action parameter

        if ("updateStatus".equals(action)) {
            String newStatus = request.getParameter("newStatus");
            if (orderIdParam == null || orderIdParam.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId or newStatus parameter.");
                return;
            }

            Connection connection = null;
            try {
                connection = new DBContext().getConnection();
                OrderDAO orderDAO = new OrderDAO();

                UUID orderId = UUID.fromString(orderIdParam);

                boolean success = orderDAO.updateOrderStatus(orderId, newStatus);

                if (success) {
                    LOGGER.log(Level.INFO, "Order {0} status updated to {1}.", new Object[]{orderId, newStatus});
                    response.sendRedirect(request.getContextPath() + "/admin/orders?message=" + java.net.URLEncoder.encode("Cập nhật trạng thái đơn hàng thành công.", java.nio.charset.StandardCharsets.UTF_8.toString()));
                } else {
                    LOGGER.log(Level.WARNING, "Failed to update order {0} status to {1}.", new Object[]{orderId, newStatus});
                    response.sendRedirect(request.getContextPath() + "/admin/orders?errorMessage=" + java.net.URLEncoder.encode("Cập nhật trạng thái đơn hàng thất bại.", java.nio.charset.StandardCharsets.UTF_8.toString()));
                }

            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid order ID format: " + orderIdParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
            } catch (SQLException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Database error in AdminOrderActionServlet", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error closing connection in AdminOrderActionServlet", e);
                    }
                }
            }
        } else if ("updatePaymentStatus".equals(action)) { // New: Handle payment status update
            String newPaymentStatus = request.getParameter("newPaymentStatus");
            if (orderIdParam == null || orderIdParam.trim().isEmpty() || newPaymentStatus == null || newPaymentStatus.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId or newPaymentStatus parameter.");
                return;
            }

            Connection connection = null;
            try {
                connection = new DBContext().getConnection();
                OrderDAO orderDAO = new OrderDAO();

                UUID orderId = UUID.fromString(orderIdParam);

                boolean success = orderDAO.updatePaymentStatus(orderId, newPaymentStatus);

                if (success) {
                    LOGGER.log(Level.INFO, "Order {0} payment status updated to {1}.", new Object[]{orderId, newPaymentStatus});
                    response.sendRedirect(request.getContextPath() + "/admin/orders?message=" + java.net.URLEncoder.encode("Cập nhật trạng thái thanh toán thành công.", java.nio.charset.StandardCharsets.UTF_8.toString()));
                } else {
                    LOGGER.log(Level.WARNING, "Failed to update order {0} payment status to {1}.", new Object[]{orderId, newPaymentStatus});
                    response.sendRedirect(request.getContextPath() + "/admin/orders?errorMessage=" + java.net.URLEncoder.encode("Cập nhật trạng thái thanh toán thất bại.", java.nio.charset.StandardCharsets.UTF_8.toString()));
                }

            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid order ID format: " + orderIdParam, e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
            } catch (SQLException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Database error in AdminOrderActionServlet", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Error closing connection in AdminOrderActionServlet", e);
                    }
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter.");
        }
    }
}
