package dao;

import model.UserAddress;
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

public class UserAddressDAO extends DBContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAddressDAO.class);

    /**
     * Thêm một địa chỉ mới cho người dùng.
     * @param address Đối tượng UserAddress cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addAddress(UserAddress address) {
        String sql = "INSERT INTO UserAddresses (AddressID, UserID, AddressType, AddressLine1, AddressLine2, City, State, PostalCode, Country, IsDefault, CreatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            address.setAddressID(newId); // Gán ID cho đối tượng trước khi thêm

            stmt.setString(1, newId.toString());
            stmt.setString(2, address.getUserID().toString());
            stmt.setString(3, address.getAddressType());
            stmt.setString(4, address.getAddressLine1());
            stmt.setString(5, address.getAddressLine2());
            stmt.setString(6, address.getCity());
            stmt.setString(7, address.getState());
            stmt.setString(8, address.getPostalCode());
            stmt.setString(9, address.getCountry());
            stmt.setBoolean(10, address.getIsDefault());
            stmt.setTimestamp(11, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added new address for user {}. Rows affected: {}", address.getUserID(), rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error adding address for user {}: {}", address.getUserID(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin một địa chỉ hiện có.
     * @param address Đối tượng UserAddress cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateAddress(UserAddress address) {
        String sql = "UPDATE UserAddresses SET AddressType = ?, AddressLine1 = ?, AddressLine2 = ?, City = ?, State = ?, PostalCode = ?, Country = ?, IsDefault = ? " +
                "WHERE AddressID = ? AND UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, address.getAddressType());
            stmt.setString(2, address.getAddressLine1());
            stmt.setString(3, address.getAddressLine2());
            stmt.setString(4, address.getCity());
            stmt.setString(5, address.getState());
            stmt.setString(6, address.getPostalCode());
            stmt.setString(7, address.getCountry());
            stmt.setBoolean(8, address.getIsDefault());
            stmt.setString(9, address.getAddressID().toString());
            stmt.setString(10, address.getUserID().toString());

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated address {} for user {}. Rows affected: {}", address.getAddressID(), address.getUserID(), rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating address {} for user {}: {}", address.getAddressID(), address.getUserID(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Xóa một địa chỉ của người dùng.
     * @param addressId ID của địa chỉ cần xóa.
     * @param userId ID của người dùng sở hữu địa chỉ.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteAddress(UUID addressId, UUID userId) {
        String sql = "DELETE FROM UserAddresses WHERE AddressID = ? AND UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, addressId.toString());
            stmt.setString(2, userId.toString());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted address {} for user {}. Rows affected: {}", addressId, userId, rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting address {} for user {}: {}", addressId, userId, e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy tất cả địa chỉ của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách các đối tượng UserAddress.
     */
    public List<UserAddress> getAddressesByUserId(UUID userId) {
        List<UserAddress> addresses = new ArrayList<>();
        String sql = "SELECT AddressID, UserID, AddressType, AddressLine1, AddressLine2, City, State, PostalCode, Country, IsDefault, CreatedDate FROM UserAddresses WHERE UserID = ? ORDER BY IsDefault DESC, CreatedDate DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToUserAddress(rs));
                }
            }
            LOGGER.info("Retrieved {} addresses for user {}.", addresses.size(), userId);
        } catch (SQLException e) {
            LOGGER.error("Error retrieving addresses for user {}: {}", userId, e.getMessage(), e);
        }
        return addresses;
    }

    /**
     * Lấy một địa chỉ cụ thể bằng ID.
     * @param addressId ID của địa chỉ.
     * @return Đối tượng UserAddress nếu tìm thấy, ngược lại null.
     */
    public UserAddress getAddressById(UUID addressId) {
        String sql = "SELECT AddressID, UserID, AddressType, AddressLine1, AddressLine2, City, State, PostalCode, Country, IsDefault, CreatedDate FROM UserAddresses WHERE AddressID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, addressId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Retrieved address with ID: {}", addressId);
                    return mapResultSetToUserAddress(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving address with ID {}: {}", addressId, e.getMessage(), e);
        }
        LOGGER.warn("No address found with ID: {}", addressId);
        return null;
    }

    /**
     * Đặt một địa chỉ làm mặc định cho người dùng, đồng thời bỏ đặt mặc định các địa chỉ khác.
     * @param addressId ID của địa chỉ muốn đặt làm mặc định.
     * @param userId ID của người dùng.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean setDefaultAddress(UUID addressId, UUID userId) {
        try {
            connection.setAutoCommit(false); // Bắt đầu transaction

            // 1. Bỏ đặt mặc định tất cả các địa chỉ khác của người dùng
            String unsetDefaultSql = "UPDATE UserAddresses SET IsDefault = 0 WHERE UserID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(unsetDefaultSql)) {
                stmt.setString(1, userId.toString());
                stmt.executeUpdate();
            }

            // 2. Đặt địa chỉ được chỉ định làm mặc định
            String setDefaultSql = "UPDATE UserAddresses SET IsDefault = 1 WHERE AddressID = ? AND UserID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(setDefaultSql)) {
                stmt.setString(1, addressId.toString());
                stmt.setString(2, userId.toString());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    LOGGER.warn("Failed to set default address {}. Address not found or not owned by user {}.", addressId, userId);
                    return false;
                }
            }

            connection.commit(); // Commit transaction
            LOGGER.info("Successfully set address {} as default for user {}.", addressId, userId);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback nếu có lỗi
            } catch (SQLException rollbackEx) {
                LOGGER.error("Error during rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            LOGGER.error("Error setting default address {} for user {}: {}", addressId, userId, e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true); // Trả lại chế độ auto-commit
            } catch (SQLException e) {
                LOGGER.error("Error resetting auto-commit: {}", e.getMessage(), e);
            }
        }
        return false;
    }

    private UserAddress mapResultSetToUserAddress(ResultSet rs) throws SQLException {
        UserAddress address = new UserAddress();
        address.setAddressID(UUID.fromString(rs.getString("AddressID")));
        address.setUserID(UUID.fromString(rs.getString("UserID")));
        address.setAddressType(rs.getString("AddressType"));
        address.setAddressLine1(rs.getString("AddressLine1"));
        address.setAddressLine2(rs.getString("AddressLine2"));
        address.setCity(rs.getString("City"));
        address.setState(rs.getString("State"));
        address.setPostalCode(rs.getString("PostalCode"));
        address.setCountry(rs.getString("Country"));
        address.setDefault(rs.getBoolean("IsDefault"));
        if (rs.getTimestamp("CreatedDate") != null) {
            address.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        }
        return address;
    }
}
