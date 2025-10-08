package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID orderID;
    private String orderNumber;
    private UUID userID;
    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentTransactionID;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String currencyCode;
    private UUID shippingAddressID;
    private UUID billingAddressID;
    private String shippingTrackingNumber;
    private String shippingCarrier;
    private String notes;
    private LocalDateTime orderDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime modifiedDate;
    private User user;
    private UserAddress shippingAddress;
    private UserAddress billingAddress;
    private List<OrderItem> orderItems;
    private List<UserCouponUsage> couponUsages;

    public Order() {
    }

    public Order(UUID orderID, String orderNumber, UUID userID, String orderStatus, String paymentStatus,
                 String paymentMethod, String paymentTransactionID, BigDecimal subtotalAmount, BigDecimal taxAmount,
                 BigDecimal shippingAmount, BigDecimal discountAmount, BigDecimal totalAmount, String currencyCode,
                 UUID shippingAddressID, UUID billingAddressID, String shippingTrackingNumber, String shippingCarrier,
                 String notes, LocalDateTime orderDate, LocalDateTime shippedDate, LocalDateTime deliveredDate,
                 LocalDateTime modifiedDate) {
        this.orderID = orderID;
        this.orderNumber = orderNumber;
        this.userID = userID;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentTransactionID = paymentTransactionID;
        this.subtotalAmount = subtotalAmount;
        this.taxAmount = taxAmount;
        this.shippingAmount = shippingAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
        this.shippingAddressID = shippingAddressID;
        this.billingAddressID = billingAddressID;
        this.shippingTrackingNumber = shippingTrackingNumber;
        this.shippingCarrier = shippingCarrier;
        this.notes = notes;
        this.orderDate = orderDate;
        this.shippedDate = shippedDate;
        this.deliveredDate = deliveredDate;
        this.modifiedDate = modifiedDate;
    }

    public UUID getOrderID() {
        return orderID;
    }

    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionID() {
        return paymentTransactionID;
    }

    public void setPaymentTransactionID(String paymentTransactionID) {
        this.paymentTransactionID = paymentTransactionID;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public UUID getShippingAddressID() {
        return shippingAddressID;
    }

    public void setShippingAddressID(UUID shippingAddressID) {
        this.shippingAddressID = shippingAddressID;
    }

    public UUID getBillingAddressID() {
        return billingAddressID;
    }

    public void setBillingAddressID(UUID billingAddressID) {
        this.billingAddressID = billingAddressID;
    }

    public String getShippingTrackingNumber() {
        return shippingTrackingNumber;
    }

    public void setShippingTrackingNumber(String shippingTrackingNumber) {
        this.shippingTrackingNumber = shippingTrackingNumber;
    }

    public String getShippingCarrier() {
        return shippingCarrier;
    }

    public void setShippingCarrier(String shippingCarrier) {
        this.shippingCarrier = shippingCarrier;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
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

    public UserAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(UserAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public UserAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(UserAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<UserCouponUsage> getCouponUsages() {
        return couponUsages;
    }

    public void setCouponUsages(List<UserCouponUsage> couponUsages) {
        this.couponUsages = couponUsages;
    }

    // Helper methods for JSP to format LocalDateTime as java.util.Date
    public java.util.Date getOrderDateAsDate() {
        return orderDate != null ? java.sql.Timestamp.valueOf(orderDate) : null;
    }

    public java.util.Date getShippedDateAsDate() {
        return shippedDate != null ? java.sql.Timestamp.valueOf(shippedDate) : null;
    }

    public java.util.Date getDeliveredDateAsDate() {
        return deliveredDate != null ? java.sql.Timestamp.valueOf(deliveredDate) : null;
    }

    public java.util.Date getModifiedDateAsDate() {
        return modifiedDate != null ? java.sql.Timestamp.valueOf(modifiedDate) : null;
    }
}