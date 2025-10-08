package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Wishlist {
    private UUID wishlistID;
    private UUID userID;
    private UUID productID;
    private LocalDateTime addedDate;
    private User user;
    private Product product;

    public Wishlist() {
    }

    public Wishlist(UUID wishlistID, UUID userID, UUID productID, LocalDateTime addedDate) {
        this.wishlistID = wishlistID;
        this.userID = userID;
        this.productID = productID;
        this.addedDate = addedDate;
    }

    public UUID getWishlistID() {
        return wishlistID;
    }

    public void setWishlistID(UUID wishlistID) {
        this.wishlistID = wishlistID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}