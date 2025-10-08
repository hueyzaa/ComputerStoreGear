package controller.servlet;

import dao.CategoryDAO;
import model.Category;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/categories", "/admin/categories/add", "/admin/categories/edit", "/admin/categories/delete"})
public class AdminCategoryServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminCategoryServlet.class.getName());
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        try {
            switch (path) {
                case "/admin/categories":
                    listCategories(request, response);
                    break;
                case "/admin/categories/add":
                    showAddForm(request, response);
                    break;
                case "/admin/categories/edit":
                    showEditForm(request, response);
                    break;
                case "/admin/categories/delete":
                    deleteCategory(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error in AdminCategoryServlet: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        try {
            switch (path) {
                case "/admin/categories/add":
                    addCategory(request, response);
                    break;
                case "/admin/categories/edit":
                    updateCategory(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error in AdminCategoryServlet: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.warning("Unauthorized access attempt to admin category management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
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
            sortBy = "CategoryName";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc";
        }

        List<Category> categoryList = categoryDAO.getCategoriesPaged(page, size, sortBy, sortOrder);
        int totalCategories = categoryDAO.getTotalCategoryCount();

        int totalPages = (int) Math.ceil((double) totalCategories / size);

        request.setAttribute("categoryList", categoryList);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/admin-categories.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        List<Category> parentCategories = categoryDAO.getAllCategoriesForAdmin(); // Lấy tất cả để chọn parent
        request.setAttribute("parentCategories", parentCategories);
        request.getRequestDispatcher("/admin-category-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String categoryIdParam = request.getParameter("id");
        if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID danh mục không được cung cấp.");
            listCategories(request, response);
            return;
        }

        try {
            UUID categoryId = UUID.fromString(categoryIdParam);
            Category category = categoryDAO.getCategoryById(categoryId);
            if (category == null) {
                request.setAttribute("errorMessage", "Không tìm thấy danh mục.");
                listCategories(request, response);
                return;
            }
            request.setAttribute("category", category);
            List<Category> parentCategories = categoryDAO.getAllCategoriesForAdmin();
            request.setAttribute("parentCategories", parentCategories);
            request.getRequestDispatcher("/admin-category-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid category ID format: " + categoryIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID danh mục không hợp lệ.");
            listCategories(request, response);
        }
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        Category category = populateCategoryFromRequest(request);
        if (category == null) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin danh mục.");
            showAddForm(request, response);
            return;
        }

        if (categoryDAO.addCategory(category) != null) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?message=" + URLEncoder.encode("Danh mục đã được thêm thành công.", StandardCharsets.UTF_8.toString()));
        } else {
            request.setAttribute("errorMessage", "Không thể thêm danh mục. Vui lòng kiểm tra tên danh mục hoặc các thông tin khác.");
            request.setAttribute("category", category); // Giữ lại dữ liệu đã nhập
            showAddForm(request, response);
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID danh mục không được cung cấp để cập nhật.");
            listCategories(request, response);
            return;
        }

        try {
            UUID categoryId = UUID.fromString(categoryIdParam);
            Category category = populateCategoryFromRequest(request);
            if (category == null) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin danh mục.");
                showEditForm(request, response);
                return;
            }
            category.setCategoryID(categoryId); // Đảm bảo ID được giữ nguyên

            if (categoryDAO.updateCategory(category)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?message=" + URLEncoder.encode("Danh mục đã được cập nhật thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật danh mục. Vui lòng thử lại.");
                request.setAttribute("category", category); // Giữ lại dữ liệu đã nhập
                showEditForm(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid category ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID danh mục không hợp lệ.");
            listCategories(request, response);
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String categoryIdParam = request.getParameter("id");
        if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID danh mục không được cung cấp để xóa.");
            listCategories(request, response);
            return;
        }

        try {
            UUID categoryId = UUID.fromString(categoryIdParam);
            if (categoryDAO.deleteCategory(categoryId)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?message=" + URLEncoder.encode("Danh mục đã được xóa thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể xóa danh mục. Vui lòng thử lại.");
                listCategories(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid category ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID danh mục không hợp lệ.");
            listCategories(request, response);
        }
    }

    private Category populateCategoryFromRequest(HttpServletRequest request) {
        try {
            Category category = new Category();
            category.setCategoryName(request.getParameter("categoryName"));
            category.setDescription(request.getParameter("description"));
            String parentCategoryId = request.getParameter("parentCategoryId");
            if (parentCategoryId != null && !parentCategoryId.trim().isEmpty()) {
                category.setParentCategoryID(UUID.fromString(parentCategoryId));
            }
            category.setActive("on".equalsIgnoreCase(request.getParameter("isActive")));
            return category;
        } catch (Exception e) {
            LOGGER.warning("Error populating category from request: " + e.getMessage());
            return null; // Trả về null nếu có lỗi parsing hoặc thiếu trường
        }
    }
}