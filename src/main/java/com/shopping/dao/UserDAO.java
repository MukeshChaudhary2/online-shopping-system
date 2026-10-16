package com.shopping.dao;

import com.shopping.model.User;
import com.shopping.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User (Customer) account operations.
 * Uses PreparedStatements exclusively to eliminate SQL injection risks.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Registers a new user into the database.
     *
     * @param user User object containing name, email, hashed password, phone, and address
     * @return true if user was successfully registered, false otherwise
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + user.getEmail(), e);
        }
        return false;
    }

    /**
     * Finds a user by their unique email address.
     *
     * @param email Customer's email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, name, email, password, phone, address, created_at FROM users WHERE email = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by email: " + email, e);
        }
        return null;
    }

    /**
     * Finds a user by primary key ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, name, email, password, phone, address, created_at FROM users WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by ID: " + userId, e);
        }
        return null;
    }

    /**
     * Updates customer profile information (name, phone, address).
     */
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET name = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile for user: " + user.getUserId(), e);
        }
        return false;
    }

    /**
     * Updates customer password hash.
     */
    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user: " + userId, e);
        }
        return false;
    }

    /**
     * Checks if an email is already registered.
     */
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email registration: " + email, e);
        }
        return false;
    }

    /**
     * Retrieves all registered customers for admin reports.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email, password, phone, address, created_at FROM users ORDER BY created_at DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
        }
        return users;
    }

    /**
     * Counts total registered users for the admin dashboard.
     */
    public int countTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting users", e);
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
