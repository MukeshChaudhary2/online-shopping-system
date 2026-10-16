package com.shopping.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing an item inside a customer's shopping cart.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int cartId;
    private int userId;
    private int productId;
    private int quantity;
    private Timestamp createdAt;

    // Additional fields populated from product join for cart display
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
    private int availableStock;
    private String productStatus;

    public CartItem() {
    }

    public CartItem(int userId, int productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    /**
     * Calculates line subtotal (quantity * productPrice)
     */
    public BigDecimal getSubtotal() {
        if (productPrice != null && quantity > 0) {
            return productPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }
}
