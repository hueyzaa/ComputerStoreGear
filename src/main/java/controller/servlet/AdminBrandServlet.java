package controller.servlet;

import dao.BrandDAO;
import model.Brand;
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
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/brands", "/admin/brands/add", "/admin/brands/edit", "/admin/brands/delete"})
public class AdminBrandServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminBrandServlet.class.getName());
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        brandDAO = new BrandDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/brands":
                listBrands(request, response);
                break;
            case "/admin/brands/add":
                showAddForm(request, response);
                break;
            case "/admin/brands/edit":
                showEditForm(request, response);
                break;
            case "/admin/brands/delete":
                deleteBrand(request, response);
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
            case "/admin/brands/add":
                addBrand(request, response);
                break;
            case "/admin/brands/edit":
                updateBrand(request, response);
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
            LOGGER.warning("Unauthorized access attempt to admin brand management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listBrands(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            sortBy = "BrandName";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc";
        }

        List<Brand> brandList = brandDAO.getBrandsPaged(page, size, sortBy, sortOrder);
        int totalBrands = brandDAO.getTotalBrandCount();

        int totalPages = (int) Math.ceil((double) totalBrands / size);

        request.setAttribute("brandList", brandList);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalBrands", totalBrands);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/admin-brands.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin-brand-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brandIdParam = request.getParameter("id");
        if (brandIdParam == null || brandIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID thương hiệu không được cung cấp.");
            listBrands(request, response);
            return;
        }

        try {
            UUID brandId = UUID.fromString(brandIdParam);
            Brand brand = brandDAO.getBrandById(brandId);
            if (brand == null) {
                request.setAttribute("errorMessage", "Không tìm thấy thương hiệu.");
                listBrands(request, response);
                return;
            }
            request.setAttribute("brand", brand);
            request.getRequestDispatcher("/admin-brand-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid brand ID format: " + brandIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID thương hiệu không hợp lệ.");
            listBrands(request, response);
        }
    }

    private void addBrand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Brand brand = populateBrandFromRequest(request);
        if (brand == null) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin thương hiệu.");
            showAddForm(request, response);
            return;
        }

        if (brandDAO.addBrand(brand) != null) {
            response.sendRedirect(request.getContextPath() + "/admin/brands?message=" + URLEncoder.encode("Thương hiệu đã được thêm thành công.", StandardCharsets.UTF_8.toString()));
        } else {
            request.setAttribute("errorMessage", "Không thể thêm thương hiệu. Vui lòng kiểm tra tên thương hiệu hoặc các thông tin khác.");
            request.setAttribute("brand", brand); // Giữ lại dữ liệu đã nhập
            showAddForm(request, response);
        }
    }

    private void updateBrand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brandIdParam = request.getParameter("brandId");
        if (brandIdParam == null || brandIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID thương hiệu không được cung cấp để cập nhật.");
            listBrands(request, response);
            return;
        }

        try {
            UUID brandId = UUID.fromString(brandIdParam);
            Brand brand = populateBrandFromRequest(request);
            if (brand == null) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin thương hiệu.");
                showEditForm(request, response);
                return;
            }
            brand.setBrandID(brandId); // Đảm bảo ID được giữ nguyên

            if (brandDAO.updateBrand(brand)) {
                response.sendRedirect(request.getContextPath() + "/admin/brands?message=" + URLEncoder.encode("Thương hiệu đã được cập nhật thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật thương hiệu. Vui lòng thử lại.");
                request.setAttribute("brand", brand); // Giữ lại dữ liệu đã nhập
                showEditForm(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid brand ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID thương hiệu không hợp lệ.");
            listBrands(request, response);
        }
    }

    private void deleteBrand(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brandIdParam = request.getParameter("id");
        if (brandIdParam == null || brandIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID thương hiệu không được cung cấp để xóa.");
            listBrands(request, response);
            return;
        }

        try {
            UUID brandId = UUID.fromString(brandIdParam);
            if (brandDAO.deleteBrand(brandId)) {
                response.sendRedirect(request.getContextPath() + "/admin/brands?message=" + URLEncoder.encode("Thương hiệu đã được xóa thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể xóa thương hiệu. Vui lòng thử lại.");
                listBrands(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid brand ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID thương hiệu không hợp lệ.");
            listBrands(request, response);
        }
    }

    private Brand populateBrandFromRequest(HttpServletRequest request) {
        try {
            Brand brand = new Brand();
            brand.setBrandName(request.getParameter("brandName"));
            brand.setDescription(request.getParameter("description"));
            brand.setLogoURL(request.getParameter("logoURL"));
            brand.setWebsite(request.getParameter("website"));
            brand.setActive("on".equalsIgnoreCase(request.getParameter("isActive")));
            return brand;
        } catch (Exception e) {
            LOGGER.warning("Error populating brand from request: " + e.getMessage());
            return null; // Trả về null nếu có lỗi parsing hoặc thiếu trường
        }
    }
}