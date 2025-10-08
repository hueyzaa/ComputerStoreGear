package controller.servlet;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.BrandDAO;
import model.Product;
import model.Category;
import model.Brand;
import model.User;

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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/products", "/admin/products/add", "/admin/products/edit", "/admin/products/delete"})
public class AdminProductServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminProductServlet.class.getName());
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
        brandDAO = new BrandDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/products":
                listProducts(request, response);
                break;
            case "/admin/products/add":
                try {
                    showAddForm(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "/admin/products/edit":
                try {
                    showEditForm(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "/admin/products/delete":
                deleteProduct(request, response);
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
            case "/admin/products/add":
                try {
                    addProduct(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "/admin/products/edit":
                try {
                    updateProduct(request, response);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
            LOGGER.warning("Unauthorized access attempt to admin product management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> productList = productDAO.getAllProductsForAdmin();
        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        List<Category> categories = categoryDAO.getAllCategoriesForAdmin();
        List<Brand> brands = brandDAO.getAllBrands();
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.getRequestDispatcher("/admin-product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String productIdParam = request.getParameter("id");
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID sản phẩm không được cung cấp.");
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            Product product = productDAO.getProductById(productId);
            if (product == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm.");
                request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
                return;
            }
            request.setAttribute("product", product);
            List<Category> categories = categoryDAO.getAllCategoriesForAdmin();
            List<Brand> brands = brandDAO.getAllBrands();
            request.setAttribute("categories", categories);
            request.setAttribute("brands", brands);
            request.getRequestDispatcher("/admin-product-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format: " + productIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
        }
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        Product product = populateProductFromRequest(request);
        if (product == null) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin sản phẩm.");
            showAddForm(request, response);
            return;
        }

        if (productDAO.addProduct(product)) {
            response.sendRedirect(request.getContextPath() + "/admin/products?message=" + URLEncoder.encode("Sản phẩm đã được thêm thành công.", StandardCharsets.UTF_8.toString()));
        } else {
            request.setAttribute("errorMessage", "Không thể thêm sản phẩm. Vui lòng kiểm tra SKU hoặc các thông tin khác.");
            request.setAttribute("product", product); // Giữ lại dữ liệu đã nhập
            showAddForm(request, response);
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String productIdParam = request.getParameter("productId");
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID sản phẩm không được cung cấp để cập nhật.");
            listProducts(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            Product product = populateProductFromRequest(request);
            if (product == null) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin sản phẩm.");
                showEditForm(request, response);
                return;
            }
            product.setProductID(productId); // Đảm bảo ID được giữ nguyên

            if (productDAO.updateProduct(product)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?message=" + URLEncoder.encode("Sản phẩm đã được cập nhật thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật sản phẩm. Vui lòng thử lại.");
                request.setAttribute("product", product); // Giữ lại dữ liệu đã nhập
                showEditForm(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            listProducts(request, response);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("id");
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID sản phẩm không được cung cấp để xóa.");
            listProducts(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            if (productDAO.deleteProduct(productId)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?message=" + URLEncoder.encode("Sản phẩm đã được xóa thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể xóa sản phẩm. Vui lòng thử lại.");
                listProducts(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            listProducts(request, response);
        }
    }

    private Product populateProductFromRequest(HttpServletRequest request) {
        try {
            Product product = new Product();
            product.setProductName(request.getParameter("productName"));
            product.setSku(request.getParameter("sku"));
            product.setCategoryID(UUID.fromString(request.getParameter("categoryId")));
            product.setBrandID(UUID.fromString(request.getParameter("brandId")));
            product.setDescription(request.getParameter("description"));
            product.setShortDescription(request.getParameter("shortDescription"));
            product.setPrice(new BigDecimal(request.getParameter("price")));
            product.setComparePrice(new BigDecimal(request.getParameter("comparePrice")));
            product.setCostPrice(new BigDecimal(request.getParameter("costPrice")));
            product.setWeight(new BigDecimal(request.getParameter("weight")));
            product.setDimensions(request.getParameter("dimensions"));
            product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            product.setMinStockLevel(Integer.parseInt(request.getParameter("minStockLevel")));
            product.setMaxStockLevel(Integer.parseInt(request.getParameter("maxStockLevel")));
            product.setActive("on".equalsIgnoreCase(request.getParameter("isActive")));
            product.setFeatured("on".equalsIgnoreCase(request.getParameter("isFeatured")));
            product.setViewCount(Integer.parseInt(request.getParameter("viewCount")));
            product.setSalesCount(Integer.parseInt(request.getParameter("salesCount")));
            product.setAverageRating(new BigDecimal(request.getParameter("averageRating")));
            product.setReviewCount(Integer.parseInt(request.getParameter("reviewCount")));
            
            // CreatedDate và ModifiedDate sẽ được set trong DAO hoặc DB
            // product.setCreatedDate(LocalDateTime.now()); 
            // product.setModifiedDate(LocalDateTime.now());

            return product;
        } catch (Exception e) {
            LOGGER.warning("Error populating product from request: " + e.getMessage());
            return null; // Trả về null nếu có lỗi parsing hoặc thiếu trường
        }
    }
}
