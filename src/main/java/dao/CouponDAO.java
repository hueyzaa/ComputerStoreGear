package dao;

import model.Coupon;
import util.DBContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouponDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouponDAO.class);

    /**
     * Thêm một mã giảm giá mới vào CSDL.
     * @param coupon Đối tượng Coupon cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addCoupon(Coupon coupon) {
        String sql = "INSERT INTO Coupons (CouponID, CouponCode, CouponName, Description, DiscountType, DiscountValue, " +
                "MinOrderAmount, MaxDiscountAmount, UsageLimit, IsActive, StartDate, EndDate, CreatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            coupon.setCouponID(newId); // Gán ID cho đối tượng trước khi thêm

            stmt.setString(1, newId.toString());
            stmt.setString(2, coupon.getCouponCode());
            stmt.setString(3, coupon.getCouponName());
            stmt.setString(4, coupon.getDescription());
            stmt.setString(5, coupon.getDiscountType());
            stmt.setBigDecimal(6, coupon.getDiscountValue());
            stmt.setBigDecimal(7, coupon.getMinOrderAmount());
            stmt.setBigDecimal(8, coupon.getMaxDiscountAmount());
            stmt.setObject(9, coupon.getUsageLimit(), java.sql.Types.INTEGER); // UsageLimit có thể null
            stmt.setBoolean(10, coupon.isActive());
            stmt.setTimestamp(11, java.sql.Timestamp.valueOf(coupon.getStartDate()));
            stmt.setTimestamp(12, java.sql.Timestamp.valueOf(coupon.getEndDate()));
            stmt.setTimestamp(13, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added new coupon with code: {}", coupon.getCouponCode());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error adding coupon {}: {}", coupon.getCouponCode(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin một mã giảm giá hiện có.
     * @param coupon Đối tượng Coupon cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateCoupon(Coupon coupon) {
        String sql = "UPDATE Coupons SET CouponCode = ?, CouponName = ?, Description = ?, DiscountType = ?, DiscountValue = ?, " +
                "MinOrderAmount = ?, MaxDiscountAmount = ?, UsageLimit = ?, IsActive = ?, StartDate = ?, EndDate = ? " +
                "WHERE CouponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, coupon.getCouponCode());
            stmt.setString(2, coupon.getCouponName());
            stmt.setString(3, coupon.getDescription());
            stmt.setString(4, coupon.getDiscountType());
            stmt.setBigDecimal(5, coupon.getDiscountValue());
            stmt.setBigDecimal(6, coupon.getMinOrderAmount());
            stmt.setBigDecimal(7, coupon.getMaxDiscountAmount());
            stmt.setObject(8, coupon.getUsageLimit(), java.sql.Types.INTEGER);
            stmt.setBoolean(9, coupon.isActive());
            stmt.setTimestamp(10, java.sql.Timestamp.valueOf(coupon.getStartDate()));
            stmt.setTimestamp(11, java.sql.Timestamp.valueOf(coupon.getEndDate()));
            stmt.setString(12, coupon.getCouponID().toString());

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated coupon with ID: {}", coupon.getCouponID());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating coupon {}: {}", coupon.getCouponID(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Xóa mềm một mã giảm giá (đặt IsActive = false).
     * @param couponId ID của mã giảm giá cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteCoupon(UUID couponId) {
        String sql = "UPDATE Coupons SET IsActive = 0 WHERE CouponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, couponId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Soft deleted coupon with ID: {}", couponId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error soft deleting coupon {}: {}", couponId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy tất cả các mã giảm giá từ CSDL.
     * @return Danh sách các đối tượng Coupon.
     */
    public List<Coupon> getAllCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT CouponID, CouponCode, CouponName, Description, DiscountType, DiscountValue, " +
                "MinOrderAmount, MaxDiscountAmount, UsageLimit, UsedCount, IsActive, StartDate, EndDate, CreatedDate FROM Coupons ORDER BY CreatedDate DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                coupons.add(mapResultSetToCoupon(rs));
            }
            LOGGER.info("Retrieved {} coupons.", coupons.size());
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all coupons: {}", e.getMessage(), e);
        }
        return coupons;
    }

    /**
     * Lấy một mã giảm giá cụ thể bằng ID.
     * @param couponId ID của mã giảm giá.
     * @return Đối tượng Coupon nếu tìm thấy, ngược lại null.
     */
    public Coupon getCouponById(UUID couponId) {
        String sql = "SELECT CouponID, CouponCode, CouponName, Description, DiscountType, DiscountValue, " +
                "MinOrderAmount, MaxDiscountAmount, UsageLimit, UsedCount, IsActive, StartDate, EndDate, CreatedDate FROM Coupons WHERE CouponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, couponId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Retrieved coupon with ID: {}", couponId);
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving coupon by ID {}: {}", couponId, e.getMessage(), e);
        }
        LOGGER.warn("No coupon found with ID: {}", couponId);
        return null;
    }

    /**
     * Lấy một mã giảm giá cụ thể bằng CouponCode.
     * @param couponCode Mã code của mã giảm giá.
     * @return Đối tượng Coupon nếu tìm thấy, ngược lại null.
     */
    public Coupon getCouponByCode(String couponCode) {
        String sql = "SELECT CouponID, CouponCode, CouponName, Description, DiscountType, DiscountValue, " +
                "MinOrderAmount, MaxDiscountAmount, UsageLimit, UsedCount, IsActive, StartDate, EndDate, CreatedDate FROM Coupons WHERE CouponCode = ? AND IsActive = 1 AND StartDate <= GETDATE() AND EndDate >= GETDATE()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, couponCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Retrieved active coupon with code: {}", couponCode);
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving coupon by code {}: {}", couponCode, e.getMessage(), e);
        }
        LOGGER.warn("No active coupon found with code: {}", couponCode);
        return null;
    }

    /**
     * Tăng số lượt sử dụng của mã giảm giá.
     * @param couponId ID của mã giảm giá.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean incrementCouponUsedCount(UUID couponId) {
        String sql = "UPDATE Coupons SET UsedCount = UsedCount + 1 WHERE CouponID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, couponId.toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error incrementing used count for coupon {}: {}", couponId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy tổng số mã giảm giá.
     * @return Tổng số mã giảm giá.
     */
    public int getTotalCouponCount() {
        String sql = "SELECT COUNT(*) FROM Coupons";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting total coupon count: {}", e.getMessage(), e);
        }
        return 0;
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setCouponID(UUID.fromString(rs.getString("CouponID")));
        coupon.setCouponCode(rs.getString("CouponCode"));
        coupon.setCouponName(rs.getString("CouponName"));
        coupon.setDescription(rs.getString("Description"));
        coupon.setDiscountType(rs.getString("DiscountType"));
        coupon.setDiscountValue(rs.getBigDecimal("DiscountValue"));
        coupon.setMinOrderAmount(rs.getBigDecimal("MinOrderAmount"));
        coupon.setMaxDiscountAmount(rs.getBigDecimal("MaxDiscountAmount"));
        coupon.setUsageLimit((Integer) rs.getObject("UsageLimit"));
        coupon.setUsedCount(rs.getInt("UsedCount"));
        coupon.setActive(rs.getBoolean("IsActive"));
        coupon.setStartDate(rs.getTimestamp("StartDate").toLocalDateTime());
        coupon.setEndDate(rs.getTimestamp("EndDate").toLocalDateTime());
        coupon.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        return coupon;
    }
}