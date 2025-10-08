package model;

import java.util.UUID;

public class ProductSpecification {
    private UUID specID;
    private UUID productID;
    private String specName;
    private String specValue;
    private String specGroup;
    private int displayOrder;
    private Product product;

    public ProductSpecification() {
    }

    public ProductSpecification(UUID specID, UUID productID, String specName, String specValue, String specGroup,
                                int displayOrder) {
        this.specID = specID;
        this.productID = productID;
        this.specName = specName;
        this.specValue = specValue;
        this.specGroup = specGroup;
        this.displayOrder = displayOrder;
    }

    public UUID getSpecID() {
        return specID;
    }

    public void setSpecID(UUID specID) {
        this.specID = specID;
    }

    public UUID getProductID() {
        return productID;
    }

    public void setProductID(UUID productID) {
        this.productID = productID;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }

    public String getSpecGroup() {
        return specGroup;
    }

    public void setSpecGroup(String specGroup) {
        this.specGroup = specGroup;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}