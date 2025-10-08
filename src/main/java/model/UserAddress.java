package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserAddress {
    private UUID addressID;
    private UUID userID;
    private String addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
    private LocalDateTime createdDate;
    private User user;
    private List<Order> shippingOrders;
    private List<Order> billingOrders;

    public UserAddress() {
    }

    public UserAddress(UUID addressID, UUID userID, String addressType, String addressLine1, String addressLine2,
                       String city, String state, String postalCode, String country, boolean isDefault,
                       LocalDateTime createdDate) {
        this.addressID = addressID;
        this.userID = userID;
        this.addressType = addressType;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
        this.createdDate = createdDate;
    }

    public UUID getAddressID() {
        return addressID;
    }

    public void setAddressID(UUID addressID) {
        this.addressID = addressID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Order> getShippingOrders() {
        return shippingOrders;
    }

    public void setShippingOrders(List<Order> shippingOrders) {
        this.shippingOrders = shippingOrders;
    }

    public List<Order> getBillingOrders() {
        return billingOrders;
    }

    public void setBillingOrders(List<Order> billingOrders) {
        this.billingOrders = billingOrders;
    }
}