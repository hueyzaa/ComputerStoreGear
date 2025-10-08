package controller.servlet;

import dao.ProductReviewDAO;
import dto.ProductReviewResponseDTO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DBContext;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/reviews", "/admin/reviews/toggle-publish", "/admin/reviews/delete"})
public class AdminProductReviewServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminProductReviewServlet.class.getName());
    private ProductReviewDAO productReviewDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            productReviewDAO = new ProductReviewDAO(new DBContext().getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        try {
            switch (path) {
                case "/admin/reviews":
                    listReviews(request, response);
                    break;
                case "/admin/reviews/toggle-publish":
                    togglePublishStatus(request, response);
                    break;
                case "/admin/reviews/delete":
                    deleteReview(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error in AdminProductReviewServlet: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Admin actions are typically GET for simplicity in this example, but can be POST
        doGet(request, response);
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.warning("Unauthorized access attempt to admin review management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listReviews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        int page = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid page number format: " + pageParam);
            }
        }

        int size = 10; // Default page size for admin
        if (sizeParam != null && !sizeParam.trim().isEmpty()) {
            try {
                size = Integer.parseInt(sizeParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid page size format: " + sizeParam);
            }
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "CreatedDate";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "desc";
        }

        List<ProductReviewResponseDTO> reviewList = productReviewDAO.getAllReviewsPaged(page, size, sortBy, sortOrder);
        int totalReviews = productReviewDAO.getTotalReviewCount();

        int totalPages = (int) Math.ceil((double) totalReviews / size);

        request.setAttribute("reviewList", reviewList);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalReviews", totalReviews);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/admin-reviews.jsp").forward(request, response);
    }

    private void togglePublishStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String reviewIdParam = request.getParameter("id");
        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID đánh giá không được cung cấp.");
            listReviews(request, response);
            return;
        }

        try {
            UUID reviewId = UUID.fromString(reviewIdParam);
            if (productReviewDAO.togglePublishStatus(reviewId)) {
                String encodedMessage = URLEncoder.encode("Trạng thái đánh giá đã được cập nhật.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/admin/reviews?message=" + encodedMessage);
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật trạng thái đánh giá. Vui lòng thử lại.");
                listReviews(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid review ID format: " + reviewIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID đánh giá không hợp lệ.");
            listReviews(request, response);
        }
    }

    private void deleteReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String reviewIdParam = request.getParameter("id");
        if (reviewIdParam == null || reviewIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID đánh giá không được cung cấp để xóa.");
            listReviews(request, response);
            return;
        }

        try {
            UUID reviewId = UUID.fromString(reviewIdParam);
            if (productReviewDAO.deleteReview(reviewId)) {
                String encodedMessage = URLEncoder.encode("Đánh giá đã được xóa thành công.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/admin/reviews?message=" + encodedMessage);
            } else {
                request.setAttribute("errorMessage", "Không thể xóa đánh giá. Vui lòng thử lại.");
                listReviews(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid review ID format for delete: " + reviewIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID đánh giá không hợp lệ.");
            listReviews(request, response);
        }
    }
}