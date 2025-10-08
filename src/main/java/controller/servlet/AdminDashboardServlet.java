package controller.servlet;

import dao.*;
import util.DBContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        try {
            connection = new DBContext().getConnection();

            ProductDAO productDAO = new ProductDAO();
            OrderDAO orderDAO = new OrderDAO();
            UserDAO userDAO = new UserDAO();
            CategoryDAO categoryDAO = new CategoryDAO();
            BrandDAO brandDAO = new BrandDAO();
            ProductReviewDAO productReviewDAO = new ProductReviewDAO(connection);
            CouponDAO couponDAO = new CouponDAO();
            InventoryTransactionDAO inventoryTransactionDAO = new InventoryTransactionDAO();

            request.setAttribute("totalProducts", productDAO.getTotalProductCount());
            request.setAttribute("totalOrders", orderDAO.getTotalOrderCount());
            request.setAttribute("totalUsers", userDAO.getTotalUserCount());
            request.setAttribute("totalCategories", categoryDAO.getTotalCategoryCount());
            request.setAttribute("totalBrands", brandDAO.getTotalBrandCount());
            request.setAttribute("totalReviews", productReviewDAO.getTotalReviewCount());
            request.setAttribute("totalCoupons", couponDAO.getTotalCouponCount());
            request.setAttribute("totalInventoryTransactions", inventoryTransactionDAO.getTotalInventoryTransactionCount());
            request.setAttribute("totalPendingOrders", orderDAO.getTotalPendingOrderCount());
            request.setAttribute("totalProcessingOrders", orderDAO.getTotalProcessingOrderCount());

            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database error in AdminDashboardServlet", e);
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection in AdminDashboardServlet", e);
                }
            }
        }
    }
}
