package controller.servlet; // Hoặc package phù hợp với cấu trúc của bạn

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileService; // Import lớp FileService của bạn

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Servlet này phục vụ các file tĩnh (như ảnh, video) từ một thư mục lưu trữ bên ngoài.
 * Nó được thiết kế để hoạt động cùng với FileService, ánh xạ các yêu cầu URL
 * (ví dụ: /static/images/abc.jpg) đến các file vật lý trên đĩa.
 *
 * Các tính năng chính:
 * - Bảo mật: Ngăn chặn tấn công Path Traversal.
 * - Hiệu năng: Hỗ trợ caching phía client bằng các header ETag và Last-Modified.
 * - Linh hoạt: Lấy đường dẫn lưu trữ từ FileService, tương thích với các cấu hình môi trường khác nhau.
 */
@WebServlet("/static/*")
public class StaticFileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFileServlet.class);

    // Thời gian cache file trên trình duyệt (ví dụ: 7 ngày)
    private static final long CACHE_DURATION_IN_SECONDS = TimeUnit.DAYS.toSeconds(7);

    private FileService fileService;

    @Override
    public void init() throws ServletException {
        try {
            // Khởi tạo FileService để biết đường dẫn lưu trữ file gốc.
            // Sử dụng constructor với ServletContext để tận dụng cấu hình của ứng dụng.
            this.fileService = new FileService(getServletContext());
            LOGGER.info("StaticFileServlet initialized. Serving files from: {}", fileService.getLocationPath());
        } catch (Exception e) {
            LOGGER.error("Failed to initialize FileService in StaticFileServlet", e);
            throw new ServletException("Could not initialize FileService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy đường dẫn file được yêu cầu từ URL (ví dụ: /images/abc.jpg)
        String requestedPath = request.getPathInfo();

        if (requestedPath == null || requestedPath.isEmpty() || requestedPath.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not specified.");
            return;
        }

        // Tạo đường dẫn vật lý đầy đủ đến file
        Path filePath;
        try {
            // Nối đường dẫn gốc từ FileService với đường dẫn được yêu cầu
            filePath = Paths.get(fileService.getLocationPath(), requestedPath).normalize();
        } catch (Exception e) {
            LOGGER.warn("Invalid path requested: {}", requestedPath, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path.");
            return;
        }

        // *** BIỆN PHÁP BẢO MẬT QUAN TRỌNG NHẤT ***
        // Đảm bảo đường dẫn chuẩn hóa phải bắt đầu bằng đường dẫn gốc đã được cấu hình.
        // Điều này ngăn chặn các cuộc tấn công kiểu "../../../etc/passwd" (Path Traversal).
        if (!filePath.startsWith(fileService.getLocationPath())) {
            LOGGER.warn("Path Traversal attempt detected! Requested path: {}, Resolved path: {}", requestedPath, filePath);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            LOGGER.debug("File not found or is not a regular file: {}", filePath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // --- Xử lý Caching ---
        long lastModified = file.lastModified();
        String eTag = "\"" + file.getName() + "_" + lastModified + "\""; // Tạo ETag đơn giản

        // 1. Kiểm tra header 'If-None-Match' (dành cho ETag)
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            LOGGER.trace("ETag match. Sending 304 Not Modified for {}", requestedPath);
            return;
        }

        // 2. Kiểm tra header 'If-Modified-Since'
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        // Bỏ qua mili giây để so sánh chính xác
        if (ifModifiedSince != -1 && (lastModified / 1000 * 1000) <= ifModifiedSince) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            LOGGER.trace("Last-Modified match. Sending 304 Not Modified for {}", requestedPath);
            return;
        }

        // --- Thiết lập các header cho response ---
        // 3. Set các header để client có thể cache lại
        response.setHeader("ETag", eTag);
        response.setDateHeader("Last-Modified", lastModified);
        response.setHeader("Cache-Control", "public, max-age=" + CACHE_DURATION_IN_SECONDS);

        // Thiết lập loại nội dung (MIME type)
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Loại mặc định
        }
        response.setContentType(contentType);

        // Thiết lập độ dài nội dung
        response.setContentLengthLong(file.length());

        // --- Gửi nội dung file ---
        LOGGER.debug("Serving file: {} with content type: {}", filePath, contentType);
        try (OutputStream out = response.getOutputStream()) {
            Files.copy(filePath, out);
        } catch (IOException e) {
            // Lỗi có thể xảy ra nếu client đóng kết nối giữa chừng
            LOGGER.warn("Error writing file to response for {}: {}", requestedPath, e.getMessage());
        }
    }
}