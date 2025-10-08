package controller.servlet;

import dao.AuthDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/users", "/admin/users/edit"})
public class AdminUserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminUserServlet.class.getName());
    private AuthDAO authDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // AuthDAO cần EmailService và FileService, nhưng ở đây chúng ta chỉ cần nó để lấy User
        // Pass null for EmailService for now, as it's not used in getUserById or getAllUsers
        authDAO = new AuthDAO(null);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/users":
                listUsers(request, response);
                break;
            case "/admin/users/edit":
                showEditForm(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/users/edit":
                updateUser(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.warning("Unauthorized access attempt to admin user management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            sortBy = "Username";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc";
        }

        List<User> userList = authDAO.getAllUsers(page, size, sortBy, sortOrder);
        int totalUsers = authDAO.getTotalUserCount();

        int totalPages = (int) Math.ceil((double) totalUsers / size);

        request.setAttribute("userList", userList);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/admin-users.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID người dùng không được cung cấp.");
            listUsers(request, response);
            return;
        }

        try {
            UUID userId = UUID.fromString(userIdParam);
            User user = authDAO.getUserById(userId);
            if (user == null) {
                request.setAttribute("errorMessage", "Không tìm thấy người dùng.");
                listUsers(request, response);
                return;
            }
            request.setAttribute("userToEdit", user);
            request.getRequestDispatcher("/admin-user-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid user ID format: " + userIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID người dùng không hợp lệ.");
            listUsers(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdParam = request.getParameter("userId");
        String role = request.getParameter("role");
        boolean isActive = "on".equalsIgnoreCase(request.getParameter("isActive"));

        if (userIdParam == null || userIdParam.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu thông tin người dùng hoặc vai trò.");
            showEditForm(request, response); // Quay lại form với lỗi
            return;
        }

        try {
            UUID userId = UUID.fromString(userIdParam);
            if (authDAO.updateUserRoleAndStatus(userId, role, isActive)) {
                response.sendRedirect(request.getContextPath() + "/admin/users?message=Cập nhật người dùng thành công.");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật người dùng. Vui lòng thử lại.");
                showEditForm(request, response); // Quay lại form với lỗi
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid user ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID người dùng không hợp lệ.");
            showEditForm(request, response); // Quay lại form với lỗi
        }
    }
}