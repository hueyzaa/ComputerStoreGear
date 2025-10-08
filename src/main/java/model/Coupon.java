package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Coupon {
    private UUID couponID;
    private String couponCode;
    private String couponName;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private int usedCount;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdDate;
    private List<UserCouponUsage> couponUsages;

    public Coupon() {
    }

    public Coupon(UUID couponID, String couponCode, String couponName, String description, String discountType,
                  BigDecimal discountValue, BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, Integer usageLimit,
                  int usedCount, boolean isActive, LocalDateTime startDate, LocalDateTime endDate,
                  LocalDateTime createdDate) {
        this.couponID = couponID;
        this.couponCode = couponCode;
        this.couponName = couponName;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
    }

    public UUID getCouponID() {
        return couponID;
    }

    public void setCouponID(UUID couponID) {
        this.couponID = couponID;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    // Helper methods for JSP to format LocalDateTime as java.util.Date
    public java.util.Date getStartDateAsDate() {
        return startDate != null ? java.sql.Timestamp.valueOf(startDate) : null;
    }

    public java.util.Date getEndDateAsDate() {
        return endDate != null ? java.sql.Timestamp.valueOf(endDate) : null;
    }

    public java.util.Date getCreatedDateAsDate() {
        return createdDate != null ? java.sql.Timestamp.valueOf(createdDate) : null;
    }

    public List<UserCouponUsage> getCouponUsages() {
        return couponUsages;
    }

    public void setCouponUsages(List<UserCouponUsage> couponUsages) {
        this.couponUsages = couponUsages;
    }
}