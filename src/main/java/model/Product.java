package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Product {
    private UUID productID;
    private String productName;
    private String sku;
    private UUID categoryID;
    private UUID brandID;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal comparePrice;
    private BigDecimal costPrice;
    private BigDecimal weight;
    private String dimensions;
    private int stockQuantity;
    private int minStockLevel;
    private int maxStockLevel;
    private boolean isActive;
    private boolean isFeatured;
    private int viewCount;
    private int salesCount;
    private BigDecimal averageRating;
    private int reviewCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Category category;
    private Brand brand;
    private List<ProductImage> images;
    private List<ProductSpecification> specifications;
    private List<ProductReview> reviews;
    private List<ShoppingCart> shoppingCartItems;
    private List<Wishlist> wishlistItems;
    private List<OrderItem> orderItems;
    private List<InventoryTransaction> inventoryTransactions;

    public Product() {
    }

    public Product(UUID productID, String productName, String sku, UUID categoryID, UUID brandID, String description,
                   String shortDescription, BigDecimal price, BigDecimal comparePrice, BigDecimal costPrice,
                   BigDecimal weight, String dimensions, int stockQuantity, int minStockLevel, int maxStockLevel,
                   boolean isActive, boolean isFeatured, int viewCount, int salesCount, BigDecimal averageRating,
                   int reviewCount, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.productID = productID;
        this.productName = productName;
        this.sku = sku;
        this.categoryID = categoryID;
        this.brandID = brandID;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.comparePrice = comparePrice;
        this.costPrice = costPrice;
        this.weight = weight;
        this.dimensions = dimensions;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.maxStockLevel = maxStockLevel;
        this.isActive = isActive;
        this.isFeatured = isFeatured;
        this.viewCount = viewCount;
        this.salesCount = salesCount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public UUID getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(UUID categoryID) {
        this.categoryID = categoryID;
    }

    public UUID getBrandID() {
        return brandID;
    }

    public void setBrandID(UUID brandID) {
        this.brandID = brandID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(BigDecimal comparePrice) {
        this.comparePrice = comparePrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public int getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(int maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public List<ProductSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ProductSpecification> specifications) {
        this.specifications = specifications;
    }

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
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
}