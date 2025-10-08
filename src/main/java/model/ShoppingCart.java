package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShoppingCart {
    private UUID cartID;
    private UUID userID;
    private UUID productID;
    private int quantity;
    private LocalDateTime addedDate;
    private LocalDateTime modifiedDate;
    private User user;
    private Product product;

    public ShoppingCart() {
    }

    public ShoppingCart(UUID cartID, UUID userID, UUID productID, int quantity, LocalDateTime addedDate,
                        LocalDateTime modifiedDate) {
        this.cartID = cartID;
        this.userID = userID;
        this.productID = productID;
        this.quantity = quantity;
        this.addedDate = addedDate;
        this.modifiedDate = modifiedDate;
    }

    public UUID getCartID() {
        return cartID;
    }

    public void setCartID(UUID cartID) {
        this.cartID = cartID;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
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