package dao;

import model.Brand;
import util.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrandDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandDAO.class);

    /**
     * Lấy tất cả các thương hiệu từ CSDL.
     * @return danh sách các đối tượng Brand.
     */
    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName, Description, LogoURL, Website, IsActive, CreatedDate FROM Brands ORDER BY BrandName";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                brands.add(mapResultSetToBrand(rs));
            }
            LOGGER.info("Retrieved {} brands.", brands.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve brands: {}", e.getMessage(), e);
        }
        return brands;
    }

    /**
     * Lấy một thương hiệu cụ thể bằng ID (UUID).
     * @param brandId ID của thương hiệu cần tìm.
     * @return đối tượng Brand hoặc null nếu không tìm thấy.
     */
    public Brand getBrandById(UUID brandId) {
        String sql = "SELECT BrandID, BrandName, Description, LogoURL, Website, IsActive, CreatedDate FROM Brands WHERE BrandID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, brandId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Retrieved brand with ID: {}", brandId);
                    return mapResultSetToBrand(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve brand with ID {}: {}", brandId, e.getMessage(), e);
        }
        LOGGER.warn("No brand found with ID: {}", brandId);
        return null;
    }

    /**
     * Thêm một thương hiệu mới vào CSDL.
     * @param brand đối tượng Brand chứa thông tin cần thêm.
     * @return UUID của thương hiệu vừa được tạo, hoặc null nếu thất bại.
     */
    public UUID addBrand(Brand brand) {
        String sql = "INSERT INTO Brands (BrandID, BrandName, Description, LogoURL, Website, IsActive, CreatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            stmt.setString(1, newId.toString());
            stmt.setString(2, brand.getBrandName());
            stmt.setString(3, brand.getDescription());
            stmt.setString(4, brand.getLogoURL());
            stmt.setString(5, brand.getWebsite());
            stmt.setBoolean(6, brand.isActive());
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Successfully added brand '{}' with new ID: {}", brand.getBrandName(), newId);
                return newId;
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to add brand '{}': {}", brand.getBrandName(), e.getMessage(), e);
        }
        return null;
    }

    /**
     * Cập nhật thông tin một thương hiệu đã có.
     * @param brand đối tượng Brand chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE Brands SET BrandName = ?, Description = ?, LogoURL = ?, Website = ?, IsActive = ? " +
                "WHERE BrandID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, brand.getBrandName());
            stmt.setString(2, brand.getDescription());
            stmt.setString(3, brand.getLogoURL());
            stmt.setString(4, brand.getWebsite());
            stmt.setBoolean(5, brand.isActive());
            stmt.setString(6, brand.getBrandID().toString());

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Update attempt for brand ID {}. Rows affected: {}", brand.getBrandID(), rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to update brand with ID {}: {}", brand.getBrandID(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Xóa một thương hiệu (xóa mềm bằng cách set IsActive = 0).
     * @param brandId ID của thương hiệu cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteBrand(UUID brandId) {
        String sql = "UPDATE Brands SET IsActive = 0 WHERE BrandID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, brandId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Soft delete attempt for brand ID {}. Rows affected: {}", brandId, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to delete brand with ID {}: {}", brandId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy danh sách thương hiệu có phân trang và sắp xếp.
     * @param pageNumber Số trang (bắt đầu từ 1).
     * @param pageSize Kích thước trang.
     * @param sortBy Trường để sắp xếp (BrandName, CreatedDate, v.v.).
     * @param sortOrder Thứ tự sắp xếp (asc, desc).
     * @return Danh sách các đối tượng Brand.
     */
    public List<Brand> getBrandsPaged(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName, Description, LogoURL, Website, IsActive, CreatedDate FROM Brands ORDER BY " + sortBy + " " + sortOrder + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (pageNumber - 1) * pageSize);
            stmt.setInt(2, pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    brands.add(mapResultSetToBrand(rs));
                }
            }
            LOGGER.info("Retrieved {} brands for page {} size {}.", brands.size(), pageNumber, pageSize);
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve brands paged: {}", e.getMessage(), e);
        }
        return brands;
    }

    /**
     * Lấy tổng số thương hiệu.
     * @return Tổng số thương hiệu.
     */
    public int getTotalBrandCount() {
        String sql = "SELECT COUNT(*) FROM Brands";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get total brand count: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Phương thức helper để ánh xạ một hàng từ ResultSet sang đối tượng Brand.
     */
    private Brand mapResultSetToBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setBrandID(UUID.fromString(rs.getString("BrandID")));
        brand.setBrandName(rs.getString("BrandName"));
        brand.setDescription(rs.getString("Description"));
        brand.setLogoURL(rs.getString("LogoURL"));
        brand.setWebsite(rs.getString("Website"));
        brand.setActive(rs.getBoolean("IsActive"));
        if (rs.getTimestamp("CreatedDate") != null) {
            brand.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        }
        return brand;
    }
}