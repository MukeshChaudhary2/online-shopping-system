package com.shopping.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a star rating and customer review for a product.
 */
public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ratingId;
    private int userId;
    private int productId;
    private int rating; // 1 to 5
    private String review;
    private Timestamp createdAt;

    // Display helper
    private String userName;

    public Rating() {
    }

    public Rating(int userId, int productId, int rating, String review) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
    }

    public Rating(int ratingId, int userId, int productId, int rating, String review, Timestamp createdAt) {
        this.ratingId = ratingId;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "ratingId=" + ratingId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                '}';
    }
}
