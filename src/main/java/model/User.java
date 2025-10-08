package model;

import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID userID;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private boolean isEmailVerified;
    private boolean isActive;
    private String role;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime lastLoginDate;
    private List<EmailVerificationToken> emailVerificationTokens;
    private List<UserAddress> addresses;
    private List<ProductReview> productReviews;
    private List<ShoppingCart> shoppingCartItems;
    private List<Wishlist> wishlistItems;
    private List<Order> orders;
    private List<UserCouponUsage> couponUsages;
    private List<InventoryTransaction> inventoryTransactions;

    public User() {
    }

    public User(UUID userID, String username, String email, String passwordHash, String salt, String firstName,
                String lastName, String phoneNumber, LocalDate dateOfBirth, String gender, boolean isEmailVerified,
                boolean isActive, String role, LocalDateTime createdDate, LocalDateTime modifiedDate,
                LocalDateTime lastLoginDate) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.isEmailVerified = isEmailVerified;
        this.isActive = isActive;
        this.role = role;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.lastLoginDate = lastLoginDate;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public List<EmailVerificationToken> getEmailVerificationTokens() {
        return emailVerificationTokens;
    }

    public void setEmailVerificationTokens(List<EmailVerificationToken> emailVerificationTokens) {
        this.emailVerificationTokens = emailVerificationTokens;
    }

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }

    public List<ProductReview> getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(List<ProductReview> productReviews) {
        this.productReviews = productReviews;
    }

    public List<ShoppingCart> getShoppingCartItems() {
        return shoppingCartItems;
    }

    public void setShoppingCartItems(List<ShoppingCart> shoppingCartItems) {
        this.shoppingCartItems = shoppingCartItems;
    }

    public List<Wishlist> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<Wishlist> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<UserCouponUsage> getCouponUsages() {
        return couponUsages;
    }

    public void setCouponUsages(List<UserCouponUsage> couponUsages) {
        this.couponUsages = couponUsages;
    }

    public List<InventoryTransaction> getInventoryTransactions() {
        return inventoryTransactions;
    }

    public void setInventoryTransactions(List<InventoryTransaction> inventoryTransactions) {
        this.inventoryTransactions = inventoryTransactions;
    }

    // Helper methods for JSP to format LocalDateTime as java.util.Date
    public java.util.Date getCreatedDateAsDate() {
        return createdDate != null ? java.sql.Timestamp.valueOf(createdDate) : null;
    }

    public java.util.Date getModifiedDateAsDate() {
        return modifiedDate != null ? java.sql.Timestamp.valueOf(modifiedDate) : null;
    }

    public java.util.Date getLastLoginDateAsDate() {
        return lastLoginDate != null ? java.sql.Timestamp.valueOf(lastLoginDate) : null;
    }
}