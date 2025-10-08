package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryTransaction {
    private UUID transactionID;
    private UUID productID;
    private String transactionType;
    private int quantity;
    private String referenceType;
    private UUID referenceID;
    private String notes;
    private LocalDateTime createdDate;
    private UUID createdBy;
    private Product product;
    private User createdByUser;

    public InventoryTransaction() {
    }

    public InventoryTransaction(UUID transactionID, UUID productID, String transactionType, int quantity,
                                String referenceType, UUID referenceID, String notes, LocalDateTime createdDate,
                                UUID createdBy) {
        this.transactionID = transactionID;
        this.productID = productID;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceID = referenceID;
        this.notes = notes;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }

    public UUID getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(UUID transactionID) {
        this.transactionID = transactionID;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public UUID getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(UUID referenceID) {
        this.referenceID = referenceID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    // Helper method for JSP to format LocalDateTime as java.util.Date
    public java.util.Date getCreatedDateAsDate() {
        return createdDate != null ? java.sql.Timestamp.valueOf(createdDate) : null;
    }
}