package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductImage {
    private UUID imageID;
    private UUID productID;
    private String imageURL;
    private String altText;
    private int displayOrder;
    private boolean isMainImage;
    private LocalDateTime createdDate;
    private Product product;

    public ProductImage() {
    }

    public ProductImage(UUID imageID, UUID productID, String imageURL, String altText, int displayOrder,
                        boolean isMainImage, LocalDateTime createdDate) {
        this.imageID = imageID;
        this.productID = productID;
        this.imageURL = imageURL;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.isMainImage = isMainImage;
        this.createdDate = createdDate;
    }

    public UUID getImageID() {
        return imageID;
    }

    public void setImageID(UUID imageID) {
        this.imageID = imageID;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isMainImage() {
        return isMainImage;
    }

    public void setMainImage(boolean mainImage) {
        isMainImage = mainImage;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}