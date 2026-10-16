package com.shopping.dao;

import com.shopping.model.CartItem;
import com.shopping.model.Order;
import com.shopping.model.OrderItem;
import com.shopping.model.Payment;
import com.shopping.model.TrackingDetail;
import com.shopping.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Order lifecycle.
 * Implements strict ACID multi-table transactions for checkout, stock reduction, and cart purging.
 */
public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    /**
     * Executes the complete checkout workflow inside a single ACID Database Transaction.
     * 1. Validates and locks product inventory (SELECT FOR UPDATE)
     * 2. Creates the parent order record
     * 3. Inserts all order_items and decrements inventory
     * 4. Creates the payment entry
     * 5. Initializes courier tracking record
     * 6. Clears the user's shopping cart
     *
     * @param order        The Order entity containing user_id, shippingAddress, totalAmount
     * @param cartItems    List of items in the cart
     * @param paymentMode  'COD' or 'ONLINE'
     * @param courierName  Selected courier name or default
     * @return Generated order_id
     * @throws SQLException or IllegalStateException if stock is insufficient or DB error occurs
     */
    public int placeOrderTransaction(Order order, List<CartItem> cartItems, String paymentMode, String courierName)
            throws SQLException, IllegalStateException {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty when placing an order.");
        }

        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // Step 1: Validate stock with row-level locks
            String checkStockSql = "SELECT product_name, stock, status, price FROM products WHERE product_id = ? FOR UPDATE";
            try (PreparedStatement checkPs = conn.prepareStatement(checkStockSql)) {
                for (CartItem item : cartItems) {
                    checkPs.setInt(1, item.getProductId());
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("Product ID " + item.getProductId() + " not found.");
                        }
                        String status = rs.getString("status");
                        int currentStock = rs.getInt("stock");
                        String productName = rs.getString("product_name");

                        if (!"ACTIVE".equalsIgnoreCase(status)) {
                            throw new IllegalStateException("Product '" + productName + "' is no longer available.");
                        }
                        if (currentStock < item.getQuantity()) {
                            throw new IllegalStateException("Insufficient stock for '" + productName + "'. Available: " + currentStock);
                        }
                    }
                }
            }

            // Step 2: Insert into orders table
            int generatedOrderId = 0;
            String insertOrderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, order_status) VALUES (?, ?, ?, 'PENDING')";
            try (PreparedStatement orderPs = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderPs.setInt(1, order.getUserId());
                orderPs.setBigDecimal(2, order.getTotalAmount());
                orderPs.setString(3, order.getShippingAddress());
                orderPs.executeUpdate();

                try (ResultSet keys = orderPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedOrderId = keys.getInt(1);
                        order.setOrderId(generatedOrderId);
                    } else {
                        throw new SQLException("Failed to retrieve generated order_id.");
                    }
                }
            }

            // Step 3: Insert order_items and decrement stock
            String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
            try (PreparedStatement itemPs = conn.prepareStatement(insertItemSql);
                 PreparedStatement stockPs = conn.prepareStatement(updateStockSql)) {

                for (CartItem item : cartItems) {
                    // Add order item
                    itemPs.setInt(1, generatedOrderId);
                    itemPs.setInt(2, item.getProductId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setBigDecimal(4, item.getProductPrice());
                    itemPs.addBatch();

                    // Decrement stock
                    stockPs.setInt(1, item.getQuantity());
                    stockPs.setInt(2, item.getProductId());
                    stockPs.addBatch();
                }

                itemPs.executeBatch();
                stockPs.executeBatch();
            }

            // Step 4: Record payment entry
            String insertPaymentSql = "INSERT INTO payments (order_id, user_id, payment_mode, payment_status, transaction_id) " +
                                      "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement payPs = conn.prepareStatement(insertPaymentSql)) {
                payPs.setInt(1, generatedOrderId);
                payPs.setInt(2, order.getUserId());
                payPs.setString(3, paymentMode);

                if ("ONLINE".equalsIgnoreCase(paymentMode)) {
                    payPs.setString(4, "COMPLETED");
                    payPs.setString(5, "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                } else {
                    payPs.setString(4, "PENDING");
                    payPs.setString(5, null);
                }
                payPs.executeUpdate();
            }

            // Step 5: Initialize Tracking record
            String insertTrackingSql = "INSERT INTO tracking_details (order_id, courier_name, tracking_no, delivery_status) " +
                                       "VALUES (?, ?, ?, 'ORDER_PLACED')";
            try (PreparedStatement trackPs = conn.prepareStatement(insertTrackingSql)) {
                String courier = (courierName != null && !courierName.trim().isEmpty()) ? courierName.trim() : "Internal Express Logistics";
                String trackingNo = "TRK-" + generatedOrderId + "-" + (int)(Math.random() * 900000 + 100000);

                trackPs.setInt(1, generatedOrderId);
                trackPs.setString(2, courier);
                trackPs.setString(3, trackingNo);
                trackPs.executeUpdate();
            }

            // Step 6: Clear user's shopping cart
            String clearCartSql = "DELETE FROM cart WHERE user_id = ?";
            try (PreparedStatement cartPs = conn.prepareStatement(clearCartSql)) {
                cartPs.setInt(1, order.getUserId());
                cartPs.executeUpdate();
            }

            // Commit entire transaction
            conn.commit();
            LOGGER.info("Order #" + generatedOrderId + " placed successfully within transaction.");
            return generatedOrderId;

        } catch (SQLException | IllegalStateException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.log(Level.WARNING, "Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.WARNING, "Failed to close connection", closeEx);
                }
            }
        }
    }

    /**
     * Fetches complete order details including items, customer info, payment, and tracking.
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.order_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(getOrderItemsByOrderId(orderId, conn));
                    order.setPayment(getPaymentByOrderId(orderId, conn));
                    order.setTrackingDetail(getTrackingByOrderId(orderId, conn));
                    return order;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching order by ID: " + orderId, e);
        }
        return null;
    }

    /**
     * Retrieves all orders placed by a specific customer.
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(getOrderItemsByOrderId(order.getOrderId(), conn));
                    order.setPayment(getPaymentByOrderId(order.getOrderId(), conn));
                    order.setTrackingDetail(getTrackingByOrderId(order.getOrderId(), conn));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching orders for user: " + userId, e);
        }
        return list;
    }

    /**
     * Retrieves all orders in the system for admin overview.
     */
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "ORDER BY o.order_id DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(getOrderItemsByOrderId(order.getOrderId(), conn));
                order.setPayment(getPaymentByOrderId(order.getOrderId(), conn));
                order.setTrackingDetail(getTrackingByOrderId(order.getOrderId(), conn));
                list.add(order);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all orders for admin", e);
        }
        return list;
    }

    /**
     * Updates order status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED).
     * If CANCELLED, restores item stocks!
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            String oldStatusSql = "SELECT order_status FROM orders WHERE order_id = ?";
            String currentStatus = null;
            try (PreparedStatement checkPs = conn.prepareStatement(oldStatusSql)) {
                checkPs.setInt(1, orderId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        currentStatus = rs.getString("order_status");
                    }
                }
            }

            String updateSql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, newStatus);
                updatePs.setInt(2, orderId);
                updatePs.executeUpdate();
            }

            // If transitioning to CANCELLED from non-cancelled status, restore stock
            if ("CANCELLED".equalsIgnoreCase(newStatus) && !"CANCELLED".equalsIgnoreCase(currentStatus)) {
                List<OrderItem> items = getOrderItemsByOrderId(orderId, conn);
                String restoreStockSql = "UPDATE products SET stock = stock + ? WHERE product_id = ?";
                try (PreparedStatement restorePs = conn.prepareStatement(restoreStockSql)) {
                    for (OrderItem item : items) {
                        restorePs.setInt(1, item.getQuantity());
                        restorePs.setInt(2, item.getProductId());
                        restorePs.addBatch();
                    }
                    restorePs.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            LOGGER.log(Level.SEVERE, "Error updating order status for order: " + orderId, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return false;
    }

    /**
     * Counts total orders for dashboard widget.
     */
    public int countTotalOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting orders", e);
        }
        return 0;
    }

    /**
     * Counts orders matching a specific status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED).
     */
    public int countOrdersByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_status = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting orders by status: " + status, e);
        }
        return 0;
    }

    /**
     * Calculates total revenue from all non-cancelled orders.
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE order_status != 'CANCELLED'";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal rev = rs.getBigDecimal(1);
                return rev != null ? rev : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue", e);
        }
        return BigDecimal.ZERO;
    }

    private List<OrderItem> getOrderItemsByOrderId(int orderId, Connection conn) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.product_name, p.image " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImage(rs.getString("image"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    private Payment getPaymentByOrderId(int orderId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM payments WHERE order_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        }
        return null;
    }

    private TrackingDetail getTrackingByOrderId(int orderId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM tracking_details WHERE order_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        }
        return null;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setUserName(rs.getString("user_name"));
        o.setUserEmail(rs.getString("user_email"));
        o.setUserPhone(rs.getString("user_phone"));
        return o;
    }
}
