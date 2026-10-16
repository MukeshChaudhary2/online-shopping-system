package com.shopping.dao;

import com.shopping.model.Rating;
import com.shopping.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Product Ratings and Reviews.
 * Enforces business rule: Only customers who purchased the product can leave reviews.
 */
public class RatingDAO {

    private static final Logger LOGGER = Logger.getLogger(RatingDAO.class.getName());

    /**
     * Checks if the user has purchased the given product in any completed/non-cancelled order.
     */
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT 1 FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "WHERE o.user_id = ? AND oi.product_id = ? AND o.order_status != 'CANCELLED' " +
                     "LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying purchase for user " + userId + " and product " + productId, e);
        }
        return false;
    }

    /**
     * Checks if the user has already left a review for the product.
     */
    public boolean hasUserRatedProduct(int userId, int productId) {
        String sql = "SELECT 1 FROM ratings WHERE user_id = ? AND product_id = ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing rating for user " + userId + " and product " + productId, e);
        }
        return false;
    }

    /**
     * Adds a new rating and review.
     */
    public boolean addRating(Rating rating) {
        // Enforce verified purchaser rule
        if (!hasUserPurchasedProduct(rating.getUserId(), rating.getProductId())) {
            LOGGER.warning("Unauthorized review attempt by non-purchasing user " + rating.getUserId());
            return false;
        }

        String sql = "INSERT INTO ratings (user_id, product_id, rating, review) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE rating = VALUES(rating), review = VALUES(review)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating.getUserId());
            ps.setInt(2, rating.getProductId());
            ps.setInt(3, rating.getRating());
            ps.setString(4, rating.getReview());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting rating for product: " + rating.getProductId(), e);
        }
        return false;
    }

    /**
     * Retrieves all customer ratings for a specific product.
     */
    public List<Rating> getRatingsByProductId(int productId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name as user_name FROM ratings r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating r = new Rating();
                    r.setRatingId(rs.getInt("rating_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setProductId(rs.getInt("product_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setReview(rs.getString("review"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching ratings for product: " + productId, e);
        }
        return list;
    }

    /**
     * Calculates the average star rating (1 to 5) for a product.
     */
    public double getAverageRating(int productId) {
        String sql = "SELECT AVG(rating) FROM ratings WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return Math.round(avg * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating for product: " + productId, e);
        }
        return 0.0;
    }

    /**
     * Returns total review count for a product.
     */
    public int getRatingCount(int productId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE product_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting ratings for product: " + productId, e);
        }
        return 0;
    }
}
