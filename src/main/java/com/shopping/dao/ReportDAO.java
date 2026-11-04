package com.shopping.dao;

import com.shopping.model.Order;
import com.shopping.model.Product;
import com.shopping.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Admin Management Reports:
 * 1. Order Report (filtered by date range & status)
 * 2. Daily Sales Report
 * 3. Monthly Sales Report
 * 4. Low-Stock Report
 * 5. Customer Report (lifetime order volume & spend)
 */
public class ReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    /**
     * Retrieves filtered orders for the Order Report.
     */
    public List<Order> getOrderReport(String startDate, String endDate, String status) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.order_status, o.shipping_address, " +
                "u.name as user_name, u.email as user_email, u.phone as user_phone, " +
                "p.payment_mode, p.payment_status " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "LEFT JOIN payments p ON o.order_id = p.order_id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND DATE(o.order_date) >= ? ");
            params.add(startDate.trim());
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND DATE(o.order_date) <= ? ");
            params.add(endDate.trim());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND o.order_status = ? ");
            params.add(status.trim());
        }

        sql.append("ORDER BY o.order_date DESC");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    o.setOrderDate(rs.getTimestamp("order_date"));
                    o.setTotalAmount(rs.getBigDecimal("total_amount"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setShippingAddress(rs.getString("shipping_address"));
                    o.setUserName(rs.getString("user_name"));
                    o.setUserEmail(rs.getString("user_email"));
                    o.setUserPhone(rs.getString("user_phone"));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating order report", e);
        }
        return list;
    }

    /**
     * Generates daily sales summary.
     */
    public List<Map<String, Object>> getDailySalesReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as report_date, " +
                     "COUNT(order_id) as total_orders, " +
                     "SUM(total_amount) as total_revenue " +
                     "FROM orders " +
                     "WHERE order_status != 'CANCELLED' " +
                     "GROUP BY DATE(order_date) " +
                     "ORDER BY report_date DESC LIMIT 30";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("reportDate", rs.getString("report_date"));
                row.put("totalOrders", rs.getInt("total_orders"));
                row.put("totalRevenue", rs.getBigDecimal("total_revenue") != null ? rs.getBigDecimal("total_revenue") : BigDecimal.ZERO);
                list.add(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating daily sales report", e);
        }
        return list;
    }

    /**
     * Generates monthly sales summary.
     */
    public List<Map<String, Object>> getMonthlySalesReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m') as report_month, " +
                     "COUNT(order_id) as total_orders, " +
                     "SUM(total_amount) as total_revenue " +
                     "FROM orders " +
                     "WHERE order_status != 'CANCELLED' " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m') " +
                     "ORDER BY report_month DESC LIMIT 12";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("reportMonth", rs.getString("report_month"));
                row.put("totalOrders", rs.getInt("total_orders"));
                row.put("totalRevenue", rs.getBigDecimal("total_revenue") != null ? rs.getBigDecimal("total_revenue") : BigDecimal.ZERO);
                list.add(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating monthly sales report", e);
        }
        return list;
    }

    /**
     * Generates low-stock alert report.
     */
    public List<Product> getLowStockReport(int threshold) {
        ProductDAO productDAO = new ProductDAO();
        return productDAO.getLowStockProducts(threshold);
    }

    /**
     * Generates customer report showing lifetime order count and total spend.
     */
    public List<Map<String, Object>> getCustomerReport() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, u.email, u.phone, u.created_at, " +
                     "COUNT(o.order_id) as total_orders, " +
                     "COALESCE(SUM(CASE WHEN o.order_status != 'CANCELLED' THEN o.total_amount ELSE 0 END), 0) as total_spent " +
                     "FROM users u " +
                     "LEFT JOIN orders o ON u.user_id = o.user_id " +
                     "GROUP BY u.user_id, u.name, u.email, u.phone, u.created_at " +
                     "ORDER BY total_spent DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("userId", rs.getInt("user_id"));
                row.put("name", rs.getString("name"));
                row.put("email", rs.getString("email"));
                row.put("phone", rs.getString("phone"));
                row.put("registeredAt", rs.getTimestamp("created_at"));
                row.put("totalOrders", rs.getInt("total_orders"));
                row.put("totalSpent", rs.getBigDecimal("total_spent"));
                list.add(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating customer report", e);
        }
        return list;
    }
}
