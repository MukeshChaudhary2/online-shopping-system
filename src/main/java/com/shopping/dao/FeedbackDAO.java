package com.shopping.dao;

import com.shopping.model.Feedback;
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
 * Data Access Object for Customer feedback and inquiries.
 */
public class FeedbackDAO {

    private static final Logger LOGGER = Logger.getLogger(FeedbackDAO.class.getName());

    /**
     * Stores customer contact form feedback into the database.
     */
    public boolean saveFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback_messages (name, email, message) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feedback.getName());
            ps.setString(2, feedback.getEmail());
            ps.setString(3, feedback.getMessage());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving feedback from: " + feedback.getEmail(), e);
        }
        return false;
    }

    /**
     * Retrieves all feedback submissions for admin panel review.
     */
    public List<Feedback> getAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback_messages ORDER BY created_at DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Feedback f = new Feedback();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setEmail(rs.getString("email"));
                f.setMessage(rs.getString("message"));
                f.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(f);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving feedback messages", e);
        }
        return list;
    }

    /**
     * Deletes a feedback message by ID.
     */
    public boolean deleteFeedback(int id) {
        String sql = "DELETE FROM feedback_messages WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting feedback ID: " + id, e);
        }
        return false;
    }

    /**
     * Counts total feedbacks for admin notification.
     */
    public int countFeedbacks() {
        String sql = "SELECT COUNT(*) FROM feedback_messages";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting feedbacks", e);
        }
        return 0;
    }
}
