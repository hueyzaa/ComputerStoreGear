package dao;

import dto.LoginRequestDTO;
import dto.RegisterRequestDTO;
import io.github.cdimascio.dotenv.Dotenv;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import util.DBContext;
import util.EmailService;
import util.FileService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthDAO extends DBContext {
    private static final Logger logger = Logger.getLogger(AuthDAO.class.getName());
    private final EmailService emailService;

    public AuthDAO(EmailService emailService) {
        super();
        this.emailService = emailService;
        logger.info("AuthDAO initialized with EmailService: " + (emailService != null ? "OK" : "NULL"));
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String currentPassword, String hashedPassword) {
        return BCrypt.checkpw(currentPassword, hashedPassword);
    }

    public boolean checkExistEmail(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.warning("Failed to check if email exists: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkExistPhoneNumber(String phoneNumber) {
        String sql = "SELECT 1 FROM Users WHERE PhoneNumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.warning("Failed to check if phone number exists: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkExistUsername(String username) {
        String sql = "SELECT 1 FROM Users WHERE Username = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.warning("Failed to check if username exists: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkDateOfBirth(Date dob) {
        return dob.toLocalDate().isBefore(LocalDate.now().minusYears(18));
    }

    public boolean checkGender(String gender) {
        return gender != null && (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female"));
    }

    public User login(LoginRequestDTO loginRequest) throws SQLException {
        String sql = "SELECT UserID, Username, Email, PasswordHash, Salt, FirstName, LastName, PhoneNumber, DateOfBirth, Gender, IsEmailVerified, IsActive, Role, CreatedDate, ModifiedDate, LastLoginDate " +
                "FROM Users WHERE Username = ? AND IsActive = 1";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, loginRequest.getUsername());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    logger.warning("No active user found for username: " + loginRequest.getUsername());
                    throw new SQLException("Username not found or account is inactive");
                }

                String storedPasswordHash = resultSet.getString("PasswordHash");
                if (!checkPassword(loginRequest.getPassword(), storedPasswordHash)) {
                    logger.warning("Invalid password for username: " + loginRequest.getUsername());
                    throw new SQLException("Invalid password");
                }

                if (!resultSet.getBoolean("IsEmailVerified")) {
                    logger.warning("Email not verified for username: " + loginRequest.getUsername());
                    throw new SQLException("Email not verified");
                }

                var user = new User();
                user.setUserID(UUID.fromString(resultSet.getString("UserID")));
                user.setUsername(resultSet.getString("Username"));
                user.setEmail(resultSet.getString("Email"));
                user.setPasswordHash(storedPasswordHash);
                user.setSalt(resultSet.getString("Salt"));
                user.setFirstName(resultSet.getString("FirstName"));
                user.setLastName(resultSet.getString("LastName"));
                user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                Date dob = resultSet.getDate("DateOfBirth");
                if (dob != null) {
                    user.setDateOfBirth(dob.toLocalDate());
                }
                user.setGender(resultSet.getString("Gender"));
                user.setEmailVerified(resultSet.getBoolean("IsEmailVerified"));
                user.setActive(resultSet.getBoolean("IsActive"));
                user.setRole(resultSet.getString("Role"));
                user.setCreatedDate(resultSet.getTimestamp("CreatedDate").toLocalDateTime());
                user.setModifiedDate(resultSet.getTimestamp("ModifiedDate").toLocalDateTime());
                Timestamp lastLogin = resultSet.getTimestamp("LastLoginDate");
                if (lastLogin != null) {
                    user.setLastLoginDate(lastLogin.toLocalDateTime());
                }

                String updateSql = "UPDATE Users SET LastLoginDate = CURRENT_TIMESTAMP WHERE UserID = ?";
                try (Connection connUpdate = getConnection();
                     PreparedStatement updateStmt = connUpdate.prepareStatement(updateSql)) {
                    updateStmt.setString(1, user.getUserID().toString());
                    updateStmt.executeUpdate();
                }

                logger.info("User logged in successfully: " + loginRequest.getUsername());
                return user;
            }
        } catch (SQLException e) {
            logger.warning("Login failed for: " + loginRequest.getUsername() + " - " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register(RegisterRequestDTO registerRequest) {
        if (checkExistUsername(registerRequest.getUsername())) {
            logger.warning("Username already exists: " + registerRequest.getUsername());
            return false;
        }
        if (checkExistEmail(registerRequest.getEmail())) {
            logger.warning("Email already exists: " + registerRequest.getEmail());
            return false;
        }
        if (registerRequest.getPhoneNumber() != null && checkExistPhoneNumber(registerRequest.getPhoneNumber())) {
            logger.warning("Phone number already exists: " + registerRequest.getPhoneNumber());
            return false;
        }
        if (registerRequest.getDateOfBirth() != null && !checkDateOfBirth(Date.valueOf(registerRequest.getDateOfBirth()))) {
            logger.warning("User must be at least 18 years old");
            return false;
        }
        if (registerRequest.getGender() != null && !checkGender(registerRequest.getGender())) {
            logger.warning("Invalid gender: " + registerRequest.getGender());
            return false;
        }

        String passwordHash = hashPassword(registerRequest.getPassword());
        String salt = BCrypt.gensalt();

        String userSql = "INSERT INTO Users (UserID, Username, Email, PasswordHash, Salt, FirstName, LastName, PhoneNumber, DateOfBirth, Gender, Role, CreatedDate, ModifiedDate, IsActive) " +
                "OUTPUT INSERTED.UserID VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";
        try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
            userStmt.setString(1, registerRequest.getUsername());
            userStmt.setString(2, registerRequest.getEmail());
            userStmt.setString(3, passwordHash);
            userStmt.setString(4, salt);
            userStmt.setString(5, registerRequest.getFirstName());
            userStmt.setString(6, registerRequest.getLastName());
            userStmt.setString(7, registerRequest.getPhoneNumber());
            if (registerRequest.getDateOfBirth() != null) {
                userStmt.setDate(8, Date.valueOf(registerRequest.getDateOfBirth()));
            } else {
                userStmt.setNull(8, Types.DATE);
            }
            userStmt.setString(9, registerRequest.getGender());
            userStmt.setString(10, "Customer");
            userStmt.setBoolean(11, true);

            boolean hasResult = userStmt.execute();
            if (!hasResult) {
                logger.severe("No result set returned from INSERT");
                return false;
            }

            try (ResultSet rs = userStmt.getResultSet()) {
                if (!rs.next()) {
                    logger.severe("Failed to retrieve generated UserID");
                    return false;
                }
                String userIdStr = rs.getString("UserID");
                logger.info("Retrieved UserID string: " + userIdStr);
                if (userIdStr == null) {
                    logger.severe("Generated UserID is null, returning false.");
                    return false;
                }
                UUID userId = UUID.fromString(userIdStr);
                logger.info("Generated UserID: " + userId);

                UUID token = UUID.randomUUID();
                LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
                String tokenSql = "INSERT INTO EmailVerificationTokens (TokenID, UserID, Token, TokenType, ExpiryDate) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement tokenStmt = connection.prepareStatement(tokenSql)) {
                    tokenStmt.setString(1, token.toString()); // Use the same UUID for TokenID
                    tokenStmt.setString(2, userId.toString());
                    tokenStmt.setString(3, token.toString());
                    tokenStmt.setString(4, "EMAIL_VERIFICATION");
                    tokenStmt.setTimestamp(5, Timestamp.valueOf(expiryDate));
                    tokenStmt.executeUpdate();
                }

                Dotenv dotenv = Dotenv.configure().filename("save.env").ignoreIfMissing().load();
                String appBaseUrl = dotenv.get("APP_BASE_URL", "http://localhost:8080/ComputerStore");
                try {
                    HashMap<String, Object> variables = new HashMap<>();
                    variables.put("firstName", registerRequest.getFirstName());
                    variables.put("verificationLink", appBaseUrl + "/verify?token=" + token.toString());
                    logger.info("Sending verification email to: " + registerRequest.getEmail());
                    emailService.sendEmail(
                            registerRequest.getEmail(),
                            "Verify Your Email Address",
                            "email-verification",
                            variables,
                            null
                    );
                } catch (Exception e) {
                    logger.severe("Failed to send verification email: " + e.getMessage());
                    String deleteSql = "DELETE FROM Users WHERE UserID = ?";
                    try (Connection conn = getConnection();
                         PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setString(1, userId.toString());
                        deleteStmt.executeUpdate();
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    return false;
                }

                return true;
            }
        } catch (SQLException e) {
            logger.severe("Failed to register user: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyEmail(String token) {
        String sql = "SELECT TokenID, UserID, ExpiryDate, IsUsed FROM EmailVerificationTokens WHERE Token = ? AND TokenType = 'EMAIL_VERIFICATION'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("Invalid or non-existent verification token: " + token);
                    return false;
                }

                UUID tokenId = UUID.fromString(rs.getString("TokenID"));
                UUID userId = UUID.fromString(rs.getString("UserID"));
                LocalDateTime expiryDate = rs.getTimestamp("ExpiryDate").toLocalDateTime();
                boolean isUsed = rs.getBoolean("IsUsed");

                if (isUsed) {
                    logger.warning("Verification token already used: " + token);
                    return false;
                }
                if (expiryDate.isBefore(LocalDateTime.now())) {
                    logger.warning("Verification token expired: " + token);
                    return false;
                }

                String updateTokenSql = "UPDATE EmailVerificationTokens SET IsUsed = 1, UsedDate = CURRENT_TIMESTAMP WHERE TokenID = ?";
                try (Connection connUpdateToken = getConnection();
                     PreparedStatement updateTokenStmt = connUpdateToken.prepareStatement(updateTokenSql)) {
                    updateTokenStmt.setString(1, tokenId.toString());
                    updateTokenStmt.executeUpdate();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                String updateUserSql = "UPDATE Users SET IsEmailVerified = 1, ModifiedDate = CURRENT_TIMESTAMP WHERE UserID = ?";
                try (Connection conn = getConnection();
                     PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql)) {
                    updateUserStmt.setString(1, userId.toString());
                    updateUserStmt.executeUpdate();
                }

                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.severe("Failed to verify email: " + e.getMessage());
            return false;
        }
    }

    public boolean forgotPassword(String email) {
        String sql = "SELECT UserID, FirstName FROM Users WHERE Email = ? AND IsActive = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("Email not found or inactive: " + email);
                    return false;
                }

                UUID userId = UUID.fromString(rs.getString("UserID"));
                String firstName = rs.getString("FirstName");

                UUID token = UUID.randomUUID();
                LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
                String tokenSql = "INSERT INTO EmailVerificationTokens (TokenID, UserID, Token, TokenType, ExpiryDate) VALUES (?, ?, ?, ?, ?)";
                try (Connection conn = getConnection();
                     PreparedStatement tokenStmt = conn.prepareStatement(tokenSql)) {
                    tokenStmt.setString(1, token.toString()); // Set TokenID
                    tokenStmt.setString(2, userId.toString());
                    tokenStmt.setString(3, token.toString());
                    tokenStmt.setString(4, "PASSWORD_RESET");
                    tokenStmt.setTimestamp(5, Timestamp.valueOf(expiryDate));
                    tokenStmt.executeUpdate();
                }

                Dotenv dotenv = Dotenv.configure().filename("save.env").ignoreIfMissing().load();
                String appBaseUrl = dotenv.get("APP_BASE_URL", "http://localhost:8080/ComputerStore");
                HashMap<String, Object> variables = new HashMap<>();
                variables.put("firstName", firstName);
                variables.put("resetLink", appBaseUrl + "/reset-password?token=" + token.toString());
                logger.info("Sending password reset email to: " + email);
                emailService.sendEmail(
                        email,
                        "Reset Your Password",
                        "password-reset",
                        variables,
                        null
                );

                return true;
            }
        } catch (Exception e) {
            logger.severe("Failed to process forgot password: " + e.getMessage());
            return false;
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        String sql = "SELECT TokenID, UserID, ExpiryDate, IsUsed FROM EmailVerificationTokens WHERE Token = ? AND TokenType = 'PASSWORD_RESET'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("Invalid or non-existent reset token: " + token);
                    return false;
                }

                UUID tokenId = UUID.fromString(rs.getString("TokenID"));
                UUID userId = UUID.fromString(rs.getString("UserID"));
                LocalDateTime expiryDate = rs.getTimestamp("ExpiryDate").toLocalDateTime();
                boolean isUsed = rs.getBoolean("IsUsed");

                if (isUsed) {
                    logger.warning("Reset token already used: " + token);
                    return false;
                }
                if (expiryDate.isBefore(LocalDateTime.now())) {
                    logger.warning("Reset token expired: " + token);
                    return false;
                }

                String passwordHash = hashPassword(newPassword);
                String salt = BCrypt.gensalt();
                String updateUserSql = "UPDATE Users SET PasswordHash = ?, Salt = ?, ModifiedDate = CURRENT_TIMESTAMP WHERE UserID = ?";
                try (PreparedStatement updateUserStmt = connection.prepareStatement(updateUserSql)) {
                    updateUserStmt.setString(1, passwordHash);
                    updateUserStmt.setString(2, salt);
                    updateUserStmt.setString(3, userId.toString());
                    updateUserStmt.executeUpdate();
                }

                String updateTokenSql = "UPDATE EmailVerificationTokens SET IsUsed = 1, UsedDate = CURRENT_TIMESTAMP WHERE TokenID = ?";
                try (PreparedStatement updateTokenStmt = connection.prepareStatement(updateTokenSql)) {
                    updateTokenStmt.setString(1, tokenId.toString());
                    updateTokenStmt.executeUpdate();
                }

                String emailSql = "SELECT Email, FirstName FROM Users WHERE UserID = ?";
                try (Connection conn = getConnection();
                     PreparedStatement emailStmt = conn.prepareStatement(emailSql)) {
                    emailStmt.setString(1, userId.toString());
                    try (ResultSet emailRs = emailStmt.executeQuery()) {
                        if (emailRs.next()) {
                            String email = emailRs.getString("Email");
                            String firstName = emailRs.getString("FirstName");
                            HashMap<String, Object> variables = new HashMap<>();
                            variables.put("firstName", firstName);
                            logger.info("Sending password changed email to: " + email);
                            emailService.sendEmail(
                                    email,
                                    "Your Password Has Been Changed",
                                    "password-changed",
                                    variables,
                                    null
                            );
                        }
                    }
                }

                return true;
            }
        } catch (Exception e) {
            logger.severe("Failed to reset password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy thông tin người dùng bằng UserID.
     * @param userId UUID của người dùng.
     * @return Đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User getUserById(UUID userId) {
        String sql = "SELECT UserID, Username, Email, PasswordHash, Salt, FirstName, LastName, PhoneNumber, DateOfBirth, Gender, IsEmailVerified, IsActive, Role, CreatedDate, ModifiedDate, LastLoginDate FROM Users WHERE UserID = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, userId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUserID(UUID.fromString(resultSet.getString("UserID")));
                    user.setUsername(resultSet.getString("Username"));
                    user.setEmail(resultSet.getString("Email"));
                    user.setPasswordHash(resultSet.getString("PasswordHash"));
                    user.setSalt(resultSet.getString("Salt"));
                    user.setFirstName(resultSet.getString("FirstName"));
                    user.setLastName(resultSet.getString("LastName"));
                    user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                    
                    Date dob = resultSet.getDate("DateOfBirth");
                    if (dob != null) {
                        user.setDateOfBirth(dob.toLocalDate());
                    }
                    user.setGender(resultSet.getString("Gender"));
                    user.setEmailVerified(resultSet.getBoolean("IsEmailVerified"));
                    user.setActive(resultSet.getBoolean("IsActive"));
                    user.setRole(resultSet.getString("Role"));
                    user.setCreatedDate(resultSet.getTimestamp("CreatedDate").toLocalDateTime());
                    
                    Timestamp modifiedDate = resultSet.getTimestamp("ModifiedDate");
                    if (modifiedDate != null) {
                        user.setModifiedDate(modifiedDate.toLocalDateTime());
                    }
                    Timestamp lastLogin = resultSet.getTimestamp("LastLoginDate");
                    if (lastLogin != null) {
                        user.setLastLoginDate(lastLogin.toLocalDateTime());
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get user by ID: " + userId + " - " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Lấy tất cả người dùng từ cơ sở dữ liệu với phân trang và sắp xếp.
     * @param pageNumber Số trang (bắt đầu từ 1).
     * @param pageSize Kích thước trang.
     * @param sortBy Trường để sắp xếp (Username, Email, CreatedDate, v.v.).
     * @param sortOrder Thứ tự sắp xếp (asc, desc).
     * @return Danh sách các đối tượng User.
     */
    public List<User> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, Username, Email, FirstName, LastName, PhoneNumber, DateOfBirth, Gender, IsEmailVerified, IsActive, Role, CreatedDate, ModifiedDate, LastLoginDate FROM Users ORDER BY " + sortBy + " " + sortOrder + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, (pageNumber - 1) * pageSize);
            statement.setInt(2, pageSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setUserID(UUID.fromString(resultSet.getString("UserID")));
                    user.setUsername(resultSet.getString("Username"));
                    user.setEmail(resultSet.getString("Email"));
                    user.setFirstName(resultSet.getString("FirstName"));
                    user.setLastName(resultSet.getString("LastName"));
                    user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                    Date dob = resultSet.getDate("DateOfBirth");
                    if (dob != null) {
                        user.setDateOfBirth(dob.toLocalDate());
                    }
                    user.setGender(resultSet.getString("Gender"));
                    user.setEmailVerified(resultSet.getBoolean("IsEmailVerified"));
                    user.setActive(resultSet.getBoolean("IsActive"));
                    user.setRole(resultSet.getString("Role"));
                    user.setCreatedDate(resultSet.getTimestamp("CreatedDate").toLocalDateTime());
                    Timestamp modifiedDate = resultSet.getTimestamp("ModifiedDate");
                    if (modifiedDate != null) {
                        user.setModifiedDate(modifiedDate.toLocalDateTime());
                    }
                    Timestamp lastLogin = resultSet.getTimestamp("LastLoginDate");
                    if (lastLogin != null) {
                        user.setLastLoginDate(lastLogin.toLocalDateTime());
                    }
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get all users paged: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    /**
     * Lấy tổng số người dùng.
     * @return Tổng số người dùng.
     */
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM Users";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Failed to get total user count: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Cập nhật vai trò và trạng thái hoạt động của người dùng.
     * @param userId ID của người dùng cần cập nhật.
     * @param role Vai trò mới.
     * @param isActive Trạng thái hoạt động mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateUserRoleAndStatus(UUID userId, String role, boolean isActive) {
        String sql = "UPDATE Users SET Role = ?, IsActive = ?, ModifiedDate = CURRENT_TIMESTAMP WHERE UserID = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, role);
            statement.setBoolean(2, isActive);
            statement.setString(3, userId.toString());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("Failed to update user role and status for user " + userId + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     * @param user Đối tượng User chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE Users SET Username = ?, Email = ?, FirstName = ?, LastName = ?, PhoneNumber = ?, DateOfBirth = ?, Gender = ?, ModifiedDate = CURRENT_TIMESTAMP WHERE UserID = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getPhoneNumber());
            if (user.getDateOfBirth() != null) {
                statement.setDate(6, Date.valueOf(user.getDateOfBirth()));
            } else {
                statement.setNull(6, Types.DATE);
            }
            statement.setString(7, user.getGender());
            statement.setString(8, user.getUserID().toString());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.severe("Failed to update user profile for user " + user.getUserID() + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void main(String[] args) {
        
    }
}