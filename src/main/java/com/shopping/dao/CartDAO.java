package com.shopping.dao;

import com.shopping.model.CartItem;
import com.shopping.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Shopping Cart operations.
 * Handles duplicate checking, live quantity increments, and user scoping.
 */
public class CartDAO {

    private static final Logger LOGGER = Logger.getLogger(CartDAO.class.getName());

    /**
     * Adds an item to the user's cart.
     * If the item is already in the cart, increments the existing quantity.
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding to cart for user " + userId + ", product " + productId, e);
        }
        return false;
    }

    /**
     * Updates item quantity in cart.
     */
    public boolean updateQuantity(int cartId, int userId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(cartId, userId);
        }
        String sql = "UPDATE cart SET quantity = ? WHERE cart_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating cart quantity for cartId: " + cartId, e);
        }
        return false;
    }

    /**
     * Removes an individual item from the cart.
     */
    public boolean removeFromCart(int cartId, int userId) {
        String sql = "DELETE FROM cart WHERE cart_id = ? AND user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart: " + cartId, e);
        }
        return false;
    }

    /**
     * Clears all items in the user's cart (used within checkout transaction).
     */
    public boolean clearCart(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Retrieves all items in the user's cart with live product details.
     */
    public List<CartItem> getCartByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.cart_id, c.user_id, c.product_id, c.quantity, c.created_at, " +
                     "p.product_name, p.price, p.image, p.stock, p.status " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.user_id = ? " +
                     "ORDER BY c.created_at DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartId(rs.getInt("cart_id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));

                    item.setProductName(rs.getString("product_name"));
                    item.setProductPrice(rs.getBigDecimal("price"));
                    item.setProductImage(rs.getString("image"));
                    item.setAvailableStock(rs.getInt("stock"));
                    item.setProductStatus(rs.getString("status"));

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching cart for user: " + userId, e);
        }
        return list;
    }

    /**
     * Calculates the total price of all items currently in the cart.
     */
    public BigDecimal getCartTotal(int userId) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(p.price * c.quantity) as total " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.user_id = ? AND p.status = 'ACTIVE'";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal("total");
                    if (result != null) {
                        total = result;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating cart total for user: " + userId, e);
        }
        return total;
    }

    /**
     * Gets total distinct count of items in cart for navbar badge.
     */
    public int getCartItemCount(int userId) {
        String sql = "SELECT SUM(quantity) FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart count for user: " + userId, e);
        }
        return 0;
    }
}
