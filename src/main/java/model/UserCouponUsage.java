package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserCouponUsage {
    private UUID usageID;
    private UUID userID;
    private UUID couponID;
    private UUID orderID;
    private LocalDateTime usedDate;
    private User user;
    private Coupon coupon;
    private Order order;

    public UserCouponUsage() {
    }

    public UserCouponUsage(UUID usageID, UUID userID, UUID couponID, UUID orderID, LocalDateTime usedDate) {
        this.usageID = usageID;
        this.userID = userID;
        this.couponID = couponID;
        this.orderID = orderID;
        this.usedDate = usedDate;
    }

    public UUID getUsageID() {
        return usageID;
    }

    public void setUsageID(UUID usageID) {
        this.usageID = usageID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public UUID getCouponID() {
        return couponID;
    }

    public void setCouponID(UUID couponID) {
        this.couponID = couponID;
    }

    public UUID getOrderID() {
        return orderID;
    }

    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}