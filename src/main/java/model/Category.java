package model;

import model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Category {
    private UUID categoryID;
    private String categoryName;
    private String description;
    private UUID parentCategoryID;
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Category parentCategory;
    private List<Category> subCategories;
    private List<Product> products;

    public Category() {
    }

    public Category(UUID categoryID, String categoryName, String description, UUID parentCategoryID, boolean isActive,
                    LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.description = description;
        this.parentCategoryID = parentCategoryID;
        this.isActive = isActive;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public UUID getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(UUID categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getParentCategoryID() {
        return parentCategoryID;
    }

    public void setParentCategoryID(UUID parentCategoryID) {
        this.parentCategoryID = parentCategoryID;
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}