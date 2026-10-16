package com.shopping.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a Product Category.
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int categoryId;
    private String categoryName;
    private String status; // 'ACTIVE' or 'INACTIVE'
    private Timestamp createdAt;

    public Category() {
    }

    public Category(String categoryName, String status) {
        this.categoryName = categoryName;
        this.status = status;
    }

    public Category(int categoryId, String categoryName, String status, Timestamp createdAt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
