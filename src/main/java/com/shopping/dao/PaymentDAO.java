package com.shopping.dao;

import com.shopping.model.Payment;
import com.shopping.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Payment records.
 * Designed with extensibility for future online payment gateway webhook callbacks.
 */
public class PaymentDAO {

    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());

    /**
     * Retrieves payment information for an order.
     */
    public Payment getPaymentByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment pay = new Payment();
                    pay.setPaymentId(rs.getInt("payment_id"));
                    pay.setOrderId(rs.getInt("order_id"));
                    pay.setUserId(rs.getInt("user_id"));
                    pay.setPaymentMode(rs.getString("payment_mode"));
                    pay.setPaymentStatus(rs.getString("payment_status"));
                    pay.setTransactionId(rs.getString("transaction_id"));
                    pay.setPaymentDate(rs.getTimestamp("payment_date"));
                    return pay;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching payment for order: " + orderId, e);
        }
        return null;
    }

    /**
     * Updates payment status and transaction ID (e.g. from COD to COMPLETED on delivery, or gateway callback).
     */
    public boolean updatePaymentStatus(int orderId, String newStatus, String transactionId) {
        String sql = "UPDATE payments SET payment_status = ?, transaction_id = COALESCE(?, transaction_id) WHERE order_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, transactionId);
            ps.setInt(3, orderId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment status for order: " + orderId, e);
        }
        return false;
    }
}
