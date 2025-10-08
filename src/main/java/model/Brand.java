package model;

import model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Brand {
    private UUID brandID;
    private String brandName;
    private String description;
    private String logoURL;
    private String website;
    private boolean isActive;
    private LocalDateTime createdDate;
    private List<Product> products;

    public Brand() {
    }

    public Brand(UUID brandID, String brandName, String description, String logoURL, String website, boolean isActive,
                 LocalDateTime createdDate) {
        this.brandID = brandID;
        this.brandName = brandName;
        this.description = description;
        this.logoURL = logoURL;
        this.website = website;
        this.isActive = isActive;
        this.createdDate = createdDate;
    }

    public UUID getBrandID() {
        return brandID;
    }

    public void setBrandID(UUID brandID) {
        this.brandID = brandID;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}