package com.shopping.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing shipment and delivery tracking status for an order.
 */
public class TrackingDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private int trackingId;
    private int orderId;
    private String courierName;
    private String trackingNo;
    private String deliveryStatus; // ORDER_PLACED, DISPATCHED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    private Timestamp updatedAt;

    public TrackingDetail() {
    }

    public TrackingDetail(int orderId, String courierName, String trackingNo, String deliveryStatus) {
        this.orderId = orderId;
        this.courierName = courierName;
        this.trackingNo = trackingNo;
        this.deliveryStatus = deliveryStatus;
    }

    public TrackingDetail(int trackingId, int orderId, String courierName, String trackingNo, String deliveryStatus, Timestamp updatedAt) {
        this.trackingId = trackingId;
        this.orderId = orderId;
        this.courierName = courierName;
        this.trackingNo = trackingNo;
        this.deliveryStatus = deliveryStatus;
        this.updatedAt = updatedAt;
    }

    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TrackingDetail{" +
                "trackingId=" + trackingId +
                ", orderId=" + orderId +
                ", courierName='" + courierName + '\'' +
                ", trackingNo='" + trackingNo + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}
