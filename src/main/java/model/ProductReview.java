package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductReview {
    private UUID reviewID;
    private UUID productID;
    private UUID userID;
    private int rating;
    private String title;
    private String reviewText;
    private boolean isVerifiedPurchase;
    private boolean isPublished;
    private int helpfulCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Product product;
    private User user;

    public ProductReview() {
    }

    public ProductReview(UUID reviewID, UUID productID, UUID userID, int rating, String title, String reviewText,
                         boolean isVerifiedPurchase, boolean isPublished, int helpfulCount, LocalDateTime createdDate,
                         LocalDateTime modifiedDate) {
        this.reviewID = reviewID;
        this.productID = productID;
        this.userID = userID;
        this.rating = rating;
        this.title = title;
        this.reviewText = reviewText;
        this.isVerifiedPurchase = isVerifiedPurchase;
        this.isPublished = isPublished;
        this.helpfulCount = helpfulCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public UUID getReviewID() {
        return reviewID;
    }

    public void setReviewID(UUID reviewID) {
        this.reviewID = reviewID;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }

    public void setVerifiedPurchase(boolean verifiedPurchase) {
        isVerifiedPurchase = verifiedPurchase;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public int getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(int helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods for JSP to format LocalDateTime as java.util.Date
    public java.util.Date getCreatedDateAsDate() {
        return createdDate != null ? java.sql.Timestamp.valueOf(createdDate) : null;
    }

    public java.util.Date getModifiedDateAsDate() {
        return modifiedDate != null ? java.sql.Timestamp.valueOf(modifiedDate) : null;
    }
}