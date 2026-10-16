package com.shopping.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Customer Order.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String orderStatus; // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    // Additional customer details for admin reports and display
    private String userName;
    private String userEmail;
    private String userPhone;

    // Associated relations
    private List<OrderItem> items = new ArrayList<>();
    private Payment payment;
    private TrackingDetail trackingDetail;

    public Order() {
    }

    public Order(int userId, BigDecimal totalAmount, String shippingAddress, String orderStatus) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }

    public Order(int orderId, int userId, Timestamp orderDate, BigDecimal totalAmount, String shippingAddress, String orderStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public TrackingDetail getTrackingDetail() {
        return trackingDetail;
    }

    public void setTrackingDetail(TrackingDetail trackingDetail) {
        this.trackingDetail = trackingDetail;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
}
