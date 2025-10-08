package dao;

import model.ProductImage;
import util.DBContext;
import util.FileService;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductImageDAO extends DBContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageDAO.class);
    private final FileService fileService;

    public ProductImageDAO(FileService fileService) {
        this.fileService = fileService;
    }

    public boolean addProductImage(ProductImage image, InputStream imageContent) throws Exception {
        String sql = "INSERT INTO ProductImages (ImageID, ProductID, ImageURL, AltText, DisplayOrder, IsMainImage, CreatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Tạo tên file dựa trên ImageID và phần mở rộng
            String fileExtension = getFileExtension(image.getImageURL());
            String fileName = image.getImageID().toString() + fileExtension;
            // Lưu file hình ảnh bằng FileService
            fileService.saveFile(fileName, imageContent);
            // Lưu đường dẫn tương đối vào cơ sở dữ liệu
            String imageUrl = "/images/" + fileName;

            // Thiết lập các tham số cho câu lệnh SQL
            stmt.setString(1, image.getImageID().toString());
            stmt.setString(2, image.getProductID().toString());
            stmt.setString(3, imageUrl);
            stmt.setString(4, image.getAltText());
            stmt.setInt(5, image.getDisplayOrder());
            stmt.setBoolean(6, image.isMainImage());
            stmt.setTimestamp(7, image.getCreatedDate() != null
                    ? java.sql.Timestamp.valueOf(image.getCreatedDate())
                    : java.sql.Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added product image with ID: {}, URL: {}", image.getImageID(), imageUrl);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to add product image: {}", e.getMessage());
            throw e;
        }
    }

    // Lấy danh sách hình ảnh theo ProductID
    public List<ProductImage> getImagesByProductId(UUID productId) throws SQLException {
        List<ProductImage> images = new ArrayList<>();
        String sql = "SELECT ImageID, ProductID, ImageURL, AltText, DisplayOrder, IsMainImage, CreatedDate " +
                "FROM ProductImages WHERE ProductID = ? ORDER BY DisplayOrder";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productId.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductImage image = new ProductImage();
                image.setImageID(UUID.fromString(rs.getString("ImageID")));
                image.setProductID(UUID.fromString(rs.getString("ProductID")));
                image.setImageURL(rs.getString("ImageURL"));
                image.setAltText(rs.getString("AltText"));
                image.setDisplayOrder(rs.getInt("DisplayOrder"));
                image.setMainImage(rs.getBoolean("IsMainImage"));
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedDate");
                if (timestamp != null) {
                    image.setCreatedDate(timestamp.toLocalDateTime());
                }
                images.add(image);
            }
            LOGGER.info("Retrieved {} images for product ID: {}", images.size(), productId);
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve images for product ID {}: {}", productId, e.getMessage());
            throw e;
        }
        return images;
    }

    // Cập nhật thông tin hình ảnh
    public boolean updateProductImage(ProductImage image, InputStream imageContent) throws Exception {
        String sql = "UPDATE ProductImages SET ImageURL = ?, AltText = ?, DisplayOrder = ?, IsMainImage = ? WHERE ImageID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String imageUrl = image.getImageURL();

            // Nếu có nội dung hình ảnh mới, lưu file và cập nhật URL
            if (imageContent != null) {
                String fileExtension = getFileExtension(image.getImageURL());
                String fileName = image.getImageID().toString() + fileExtension;
                fileService.saveFile(fileName, imageContent);
                imageUrl = "/images/" + fileName;
            }

            stmt.setString(1, imageUrl);
            stmt.setString(2, image.getAltText());
            stmt.setInt(3, image.getDisplayOrder());
            stmt.setBoolean(4, image.isMainImage());
            stmt.setString(5, image.getImageID().toString());

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated product image with ID: {}, URL: {}", image.getImageID(), imageUrl);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to update product image with ID {}: {}", image.getImageID(), e.getMessage());
            throw e;
        }
    }

    // Xóa hình ảnh sản phẩm
    public boolean deleteProductImage(UUID imageId) throws SQLException {
        String sql = "DELETE FROM ProductImages WHERE ImageID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, imageId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted product image with ID: {}", imageId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete product image with ID {}: {}", imageId, e.getMessage());
            throw e;
        }
    }

    // Lấy hình ảnh chính của sản phẩm
    public ProductImage getMainImageByProductId(UUID productId) throws SQLException {
        String sql = "SELECT ImageID, ProductID, ImageURL, AltText, DisplayOrder, IsMainImage, CreatedDate " +
                "FROM ProductImages WHERE ProductID = ? AND IsMainImage = 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productId.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ProductImage image = new ProductImage();
                image.setImageID(UUID.fromString(rs.getString("ImageID")));
                image.setProductID(UUID.fromString(rs.getString("ProductID")));
                image.setImageURL(rs.getString("ImageURL"));
                image.setAltText(rs.getString("AltText"));
                image.setDisplayOrder(rs.getInt("DisplayOrder"));
                image.setMainImage(rs.getBoolean("IsMainImage"));
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedDate");
                if (timestamp != null) {
                    image.setCreatedDate(timestamp.toLocalDateTime());
                }
                LOGGER.info("Retrieved main image for product ID: {}", productId);
                return image;
            }
            LOGGER.info("No main image found for product ID: {}", productId);
            return null;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve main image for product ID {}: {}", productId, e.getMessage());
            throw e;
        }
    }

    // Helper method để lấy phần mở rộng file
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg"; // Mặc định là .jpg nếu không có phần mở rộng
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}