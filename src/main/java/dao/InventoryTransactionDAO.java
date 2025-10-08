package dao;

import model.InventoryTransaction;
import model.Product;
import model.User;
import util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryTransactionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryTransactionDAO.class);
    private final ProductDAO productDAO = new ProductDAO();
    private final AuthDAO authDAO; // Để lấy thông tin người dùng tạo giao dịch

    public InventoryTransactionDAO() {
        // AuthDAO cần EmailService và FileService, nhưng ở đây chúng ta chỉ cần nó để lấy User
        this.authDAO = new AuthDAO(null); // Pass null for EmailService for now
    }

    /**
     * Thêm một giao dịch tồn kho mới.
     * @param transaction Đối tượng InventoryTransaction cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addTransaction(InventoryTransaction transaction, Connection connection) {
        String sql = "INSERT INTO InventoryTransactions (TransactionID, ProductID, TransactionType, Quantity, " +
                "ReferenceType, ReferenceID, Notes, CreatedDate, CreatedBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            transaction.setTransactionID(newId); // Gán ID cho đối tượng trước khi thêm

            stmt.setString(1, newId.toString());
            stmt.setString(2, transaction.getProductID().toString());
            stmt.setString(3, transaction.getTransactionType());
            stmt.setInt(4, transaction.getQuantity());
            stmt.setString(5, transaction.getReferenceType());
            stmt.setString(6, transaction.getReferenceID() != null ? transaction.getReferenceID().toString() : null);
            stmt.setString(7, transaction.getNotes());
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(9, transaction.getCreatedBy() != null ? transaction.getCreatedBy().toString() : null);

            LOGGER.info("InventoryTransactionDAO: Executing update for transaction ID {}.", newId);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("InventoryTransactionDAO: Update executed. Rows affected: {} for transaction ID {}.", rowsAffected, newId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error adding inventory transaction for product {}: {}", transaction.getProductID(), e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy tất cả các giao dịch tồn kho.
     * @return Danh sách các đối tượng InventoryTransaction.
     */
    public List<InventoryTransaction> getAllTransactions(Connection connection) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.TransactionID, it.ProductID, it.TransactionType, it.Quantity, " +
                "it.ReferenceType, it.ReferenceID, it.Notes, it.CreatedDate, it.CreatedBy, " +
                "p.ProductName, u.Username AS CreatedByUsername " +
                "FROM InventoryTransactions it " +
                "JOIN Products p ON it.ProductID = p.ProductID " +
                "LEFT JOIN Users u ON it.CreatedBy = u.UserID " +
                "ORDER BY it.CreatedDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                InventoryTransaction transaction = mapResultSetToInventoryTransaction(rs);
                // Set Product object
                Product product = new Product();
                product.setProductID(UUID.fromString(rs.getString("ProductID")));
                product.setProductName(rs.getString("ProductName"));
                transaction.setProduct(product);

                // Set User object (CreatedBy)
                String createdByUserId = rs.getString("CreatedBy");
                if (createdByUserId != null) {
                    User createdByUser = new User();
                    createdByUser.setUserID(UUID.fromString(createdByUserId));
                    createdByUser.setUsername(rs.getString("CreatedByUsername"));
                    transaction.setCreatedByUser(createdByUser);
                }
                transactions.add(transaction);
            }
            LOGGER.info("Retrieved {} inventory transactions.", transactions.size());
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all inventory transactions: {}", e.getMessage(), e);
        }
        return transactions;
    }

    /**
     * Lấy tổng số giao dịch tồn kho.
     * @return Tổng số giao dịch tồn kho.
     */
    public int getTotalInventoryTransactionCount() {
        String sql = "SELECT COUNT(*) FROM InventoryTransactions";
        try (Connection connection = new DBContext().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("Error getting total inventory transaction count: {}", e.getMessage(), e);
        }
        return 0;
    }

    private InventoryTransaction mapResultSetToInventoryTransaction(ResultSet rs) throws SQLException {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionID(UUID.fromString(rs.getString("TransactionID")));
        transaction.setProductID(UUID.fromString(rs.getString("ProductID")));
        transaction.setTransactionType(rs.getString("TransactionType"));
        transaction.setQuantity(rs.getInt("Quantity"));
        transaction.setReferenceType(rs.getString("ReferenceType"));
        String refId = rs.getString("ReferenceID");
        if (refId != null) {
            transaction.setReferenceID(UUID.fromString(refId));
        }
        transaction.setNotes(rs.getString("Notes"));
        transaction.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        String createdBy = rs.getString("CreatedBy");
        if (createdBy != null) {
            transaction.setCreatedBy(UUID.fromString(createdBy));
        }
        return transaction;
    }
}