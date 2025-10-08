package controller.servlet;

import dao.ProductReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ProductReview;
import model.User;

import util.DBContext;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/product/review/add"})
public class ProductReviewServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProductReviewServlet.class.getName());
    private ProductReviewDAO productReviewDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // productReviewDAO is now initialized per request in doPost
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?message=Vui lòng đăng nhập để gửi đánh giá.");
            return;
        }

        Connection connection = null;
        try {
            connection = new DBContext().getConnection();
            productReviewDAO = new ProductReviewDAO(connection);
            
            String productIdParam = request.getParameter("productId");
            String ratingParam = request.getParameter("rating");
            String title = request.getParameter("title");
            String reviewText = request.getParameter("reviewText");

            if (productIdParam == null || productIdParam.trim().isEmpty() ||
                ratingParam == null || ratingParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu thông tin sản phẩm hoặc điểm đánh giá.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            UUID productId = UUID.fromString(productIdParam);
            int rating = Integer.parseInt(ratingParam);

            if (rating < 1 || rating > 5) {
                request.setAttribute("errorMessage", "Điểm đánh giá phải từ 1 đến 5.");
                request.getRequestDispatcher("/product-detail?id=" + productId).forward(request, response);
                return;
            }

            ProductReview review = new ProductReview();
            review.setProductID(productId);
            review.setUserID(currentUser.getUserID());
            review.setRating(rating);
            review.setTitle(title != null ? title.trim() : null);
            review.setReviewText(reviewText != null ? reviewText.trim() : null);
            review.setVerifiedPurchase(false); // Cần logic để xác định mua hàng đã xác minh
            review.setPublished(true); // Mặc định là chưa duyệt, cần admin duyệt

            if (productReviewDAO.addReview(review)) {
                // Cập nhật lại điểm trung bình của sản phẩm sau khi thêm review
                productReviewDAO.updateProductRating(productId);
                String message = URLEncoder.encode("Đánh giá của bạn đã được gửi và đang chờ duyệt.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/product-detail?id=" + productId + "&message=" + message);
            } else {
                request.setAttribute("errorMessage", "Không thể gửi đánh giá. Vui lòng thử lại.");
                request.getRequestDispatcher("/product-detail?id=" + productId).forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID or rating format: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm hoặc điểm đánh giá không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.severe("Error adding product review: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi gửi đánh giá: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.severe("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
