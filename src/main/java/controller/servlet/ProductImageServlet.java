package controller.servlet;

import dao.ProductImageDAO;
import model.ProductImage;
import model.User;
import util.FileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/product-images", "/admin/product-images/upload", "/admin/product-images/delete"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10, // 10MB
                 maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ProductImageServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProductImageServlet.class.getName());
    private ProductImageDAO productImageDAO;
    private FileService fileService;

    @Override
    public void init() throws ServletException {
        super.init();
        fileService = new FileService(getServletContext());
        productImageDAO = new ProductImageDAO(fileService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/product-images":
                listProductImages(request, response);
                break;
            case "/admin/product-images/delete":
                deleteProductImage(request, response);
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
            case "/admin/product-images/upload":
                uploadProductImage(request, response);
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
            LOGGER.warning("Unauthorized access attempt to product image management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listProductImages(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("productId");
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID sản phẩm không được cung cấp.");
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            request.setAttribute("productId", productId);
            List<ProductImage> images = productImageDAO.getImagesByProductId(productId);
            request.setAttribute("images", images);
            request.getRequestDispatcher("/admin-product-images.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format: " + productIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.severe("Error listing product images: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách hình ảnh: " + e.getMessage());
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
        }
    }

    private void uploadProductImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter("productId");
        String altText = request.getParameter("altText");
        String displayOrderParam = request.getParameter("displayOrder");
        boolean isMainImage = "on".equalsIgnoreCase(request.getParameter("isMainImage"));

        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID sản phẩm không được cung cấp.");
            listProductImages(request, response); // Quay lại trang danh sách hình ảnh
            return;
        }

        UUID productId = null;
        try {
            productId = UUID.fromString(productIdParam);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format for upload: " + productIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            listProductImages(request, response);
            return;
        }

        int displayOrder = 0;
        if (displayOrderParam != null && !displayOrderParam.trim().isEmpty()) {
            try {
                displayOrder = Integer.parseInt(displayOrderParam);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid display order format: " + displayOrderParam);
                request.setAttribute("errorMessage", "Thứ tự hiển thị không hợp lệ.");
                listProductImages(request, response);
                return;
            }
        }

        try {
            Part filePart = request.getPart("imageFile");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("errorMessage", "Vui lòng chọn một tệp hình ảnh để tải lên.");
                listProductImages(request, response);
                return;
            }

            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("errorMessage", "Tên tệp không hợp lệ.");
                listProductImages(request, response);
                return;
            }

            UUID imageId = UUID.randomUUID();
            ProductImage image = new ProductImage();
            image.setImageID(imageId);
            image.setProductID(productId);
            image.setImageURL(fileName); // Tên file tạm thời, sẽ được FileService xử lý
            image.setAltText(altText);
            image.setDisplayOrder(displayOrder);
            image.setMainImage(isMainImage);
            image.setCreatedDate(LocalDateTime.now());

            try (InputStream fileContent = filePart.getInputStream()) {
                if (productImageDAO.addProductImage(image, fileContent)) {
                    response.sendRedirect(request.getContextPath() + "/admin/product-images?productId=" + productId + "&message=" + URLEncoder.encode("Hình ảnh đã được tải lên thành công.", StandardCharsets.UTF_8.toString()));
                } else {
                    request.setAttribute("errorMessage", "Không thể tải lên hình ảnh. Vui lòng thử lại.");
                    listProductImages(request, response);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error uploading product image: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi tải lên hình ảnh: " + e.getMessage());
            listProductImages(request, response);
        }
    }

    private void deleteProductImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String imageIdParam = request.getParameter("id");
        String productIdParam = request.getParameter("productId"); // Cần để chuyển hướng lại

        if (imageIdParam == null || imageIdParam.trim().isEmpty() || productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu ID hình ảnh hoặc ID sản phẩm để xóa.");
            request.getRequestDispatcher("/admin-products.jsp").forward(request, response);
            return;
        }

        try {
            UUID imageId = UUID.fromString(imageIdParam);
            UUID productId = UUID.fromString(productIdParam);

            if (productImageDAO.deleteProductImage(imageId)) {
                response.sendRedirect(request.getContextPath() + "/admin/product-images?productId=" + productId + "&message=" + URLEncoder.encode("Hình ảnh đã được xóa thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể xóa hình ảnh. Vui lòng thử lại.");
                listProductImages(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid image ID or product ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID hình ảnh hoặc ID sản phẩm không hợp lệ.");
            listProductImages(request, response);
        } catch (Exception e) {
            LOGGER.severe("Error deleting product image: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi xóa hình ảnh: " + e.getMessage());
            listProductImages(request, response);
        }
    }
}
