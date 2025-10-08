package dao;

import model.UserCouponUsage;
import util.DBContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCouponUsageDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCouponUsageDAO.class);

    /**
     * Ghi lại việc sử dụng mã giảm giá của người dùng.
     * @param userId ID của người dùng.
     * @param couponId ID của mã giảm giá.
     * @param orderId ID của đơn hàng (có thể null nếu mã giảm giá được áp dụng trước khi tạo đơn hàng).
     * @return true nếu ghi lại thành công, false nếu thất bại.
     */
    public boolean addCouponUsage(UUID userId, UUID couponId, UUID orderId) {
        String sql = "INSERT INTO UserCouponUsages (UsageID, UserID, CouponID, OrderID, UsedDate) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            stmt.setString(1, newId.toString());
            stmt.setString(2, userId.toString());
            stmt.setString(3, couponId.toString());
            if (orderId != null) {
                stmt.setString(4, orderId.toString());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Recorded coupon usage for user {} coupon {}. Rows affected: {}", userId, couponId, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error adding coupon usage for user {} coupon {}: {}", userId, couponId, e.getMessage(), e);
        }
        return false;
    }
}
