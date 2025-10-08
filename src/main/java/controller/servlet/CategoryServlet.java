package controller.servlet;

import dao.CategoryDAO;
import model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServlet.class);
    private CategoryDAO categoryDAO;

    @Override
    public void init() {
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        try {
            switch (action) {
                case "/admin/categories/add":
                    showForm(request, response);
                    break;
                case "/admin/categories/edit":
                    showForm(request, response);
                    break;
                case "/admin/categories/delete":
                    deleteCategory(request, response);
                    break;
                default:
                    listCategories(request, response);
                    break;
            }
        } catch (SQLException ex) {
            try {
                handleSQLException(ex, action, request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        try {
            switch (action) {
                case "/admin/categories/add":
                    saveCategory(request, response);
                    break;
                case "/admin/categories/edit":
                    saveCategory(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/categories");
                    break;
            }
        } catch (SQLException ex) {
            try {
                handleSQLException(ex, action, request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception ex) {
            handleGenericException(ex, action, request, response);
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Category> listCategories = categoryDAO.getAllCategoriesForAdmin();
        request.setAttribute("listCategories", listCategories);
        request.getRequestDispatcher("/WEB-INF/views/admin/categoryList.jsp").forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String idStr = request.getParameter("id");
        Category category = new Category();
        category.setActive(true); // Mặc định active cho form tạo mới

        if (idStr != null && !idStr.trim().isEmpty()) { // Trường hợp Edit
            try {
                UUID id = UUID.fromString(idStr);
                category = categoryDAO.getCategoryById(id);
                if (category == null) {
                    redirectToCategoryList(request, response, "error", "CategoryNotFound");
                    return;
                }
            } catch (IllegalArgumentException e) {
                redirectToCategoryList(request, response, "error", "InvalidIDFormat");
                return;
            }
        }

        List<Category> allCategories = categoryDAO.getAllCategoriesForAdmin();
        request.setAttribute("category", category);
        request.setAttribute("listCategories", allCategories);
        request.getRequestDispatcher("/WEB-INF/views/admin/categoryForm.jsp").forward(request, response);
    }

    private void saveCategory(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        Category category = parseCategoryFromRequest(request);

        // Validate
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            request.setAttribute("errorMessage", "Category name is required.");
            request.setAttribute("category", category); // Giữ lại dữ liệu đã nhập
            showForm(request, response); // Quay lại form
            return;
        }

        boolean success;
        if (category.getCategoryID() == null) { // Add new
            UUID newId = categoryDAO.addCategory(category);
            success = (newId != null);
        } else { // Update existing
            success = categoryDAO.updateCategory(category);
        }

        if (success) {
            redirectToCategoryList(request, response, "success", (category.getCategoryID() == null ? "CategoryAdded" : "CategoryUpdated"));
        } else {
            request.setAttribute("errorMessage", "Failed to save category. Please try again.");
            request.setAttribute("category", category);
            showForm(request, response);
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            redirectToCategoryList(request, response, "error", "MissingID");
            return;
        }
        try {
            UUID id = UUID.fromString(idStr);
            categoryDAO.deleteCategory(id);
            redirectToCategoryList(request, response, "success", "CategoryDeleted");
        } catch (IllegalArgumentException e) {
            redirectToCategoryList(request, response, "error", "InvalidIDFormat");
        }
    }

    private Category parseCategoryFromRequest(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        String name = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String parentIdStr = request.getParameter("parentCategoryID");
        boolean isActive = "on".equalsIgnoreCase(request.getParameter("isActive"));

        Category category = new Category();
        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                category.setCategoryID(UUID.fromString(idStr));
            }
            if (parentIdStr != null && !parentIdStr.trim().isEmpty() && !parentIdStr.equals("0")) { // Giả sử "0" là giá trị cho "No Parent"
                category.setParentCategoryID(UUID.fromString(parentIdStr));
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid UUID format in request parameters: id={}, parentId={}", idStr, parentIdStr);
            // Có thể đặt một cờ lỗi trong request để xử lý ở tầng trên
        }

        category.setCategoryName(name);
        category.setDescription(description);
        category.setActive(isActive);
        return category;
    }

    private void redirectToCategoryList(HttpServletRequest request, HttpServletResponse response, String messageType, String messageValue) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/categories?" + messageType + "=" + messageValue);
    }

    private void handleSQLException(SQLException ex, String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        LOGGER.error("Database error in action {}: {}", action, ex.getMessage(), ex);
        request.setAttribute("errorMessage", "A database error occurred: " + ex.getMessage());
        showForm(request, response); // Cố gắng hiển thị lại form nếu có thể
    }

    private void handleGenericException(Exception ex, String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.error("Unexpected error in action {}: {}", action, ex.getMessage(), ex);
        request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
    }
}