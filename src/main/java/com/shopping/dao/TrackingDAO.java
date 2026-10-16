package com.shopping.dao;

import com.shopping.model.TrackingDetail;
import com.shopping.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Order Courier Tracking updates and lookups.
 */
public class TrackingDAO {

    private static final Logger LOGGER = Logger.getLogger(TrackingDAO.class.getName());

    /**
     * Retrieves tracking information for a specific order.
     */
    public TrackingDetail getTrackingByOrderId(int orderId) {
        String sql = "SELECT * FROM tracking_details WHERE order_id = ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TrackingDetail td = new TrackingDetail();
                    td.setTrackingId(rs.getInt("tracking_id"));
                    td.setOrderId(rs.getInt("order_id"));
                    td.setCourierName(rs.getString("courier_name"));
                    td.setTrackingNo(rs.getString("tracking_no"));
                    td.setDeliveryStatus(rs.getString("delivery_status"));
                    td.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return td;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching tracking for order: " + orderId, e);
        }
        return null;
    }

    /**
     * Updates tracking details and syncs delivery status with order status.
     */
    public boolean updateTracking(int orderId, String courierName, String trackingNo, String deliveryStatus) {
        String sql = "INSERT INTO tracking_details (order_id, courier_name, tracking_no, delivery_status) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE courier_name = VALUES(courier_name), " +
                     "tracking_no = VALUES(tracking_no), delivery_status = VALUES(delivery_status)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, courierName);
            ps.setString(3, trackingNo);
            ps.setString(4, deliveryStatus);

            boolean updated = ps.executeUpdate() > 0;

            // Sync delivery_status to orders table
            String syncOrderSql = "UPDATE orders SET order_status = CASE " +
                                  "WHEN ? = 'DELIVERED' THEN 'DELIVERED' " +
                                  "WHEN ? = 'CANCELLED' THEN 'CANCELLED' " +
                                  "WHEN ? = 'DISPATCHED' OR ? = 'IN_TRANSIT' OR ? = 'OUT_FOR_DELIVERY' THEN 'SHIPPED' " +
                                  "ELSE order_status END WHERE order_id = ?";
            try (PreparedStatement syncPs = conn.prepareStatement(syncOrderSql)) {
                syncPs.setString(1, deliveryStatus);
                syncPs.setString(2, deliveryStatus);
                syncPs.setString(3, deliveryStatus);
                syncPs.setString(4, deliveryStatus);
                syncPs.setString(5, deliveryStatus);
                syncPs.setInt(6, orderId);
                syncPs.executeUpdate();
            }

            return updated;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating tracking for order: " + orderId, e);
        }
        return false;
    }
}
