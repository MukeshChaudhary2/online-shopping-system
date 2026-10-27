package com.shopping.dao;

import com.shopping.model.Category;
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
 * Data Access Object for Product Categories.
 */
public class CategoryDAO {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    /**
     * Adds a new category to the database.
     */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (category_name, status) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getStatus() != null ? category.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding category: " + category.getCategoryName(), e);
        }
        return false;
    }

    /**
     * Updates an existing category.
     */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET category_name = ?, status = ? WHERE category_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getStatus());
            ps.setInt(3, category.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + category.getCategoryId(), e);
        }
        return false;
    }

    /**
     * Deletes a category if not referenced by products.
     */
    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category: " + categoryId, e);
        }
        return false;
    }

    /**
     * Retrieves category by ID.
     */
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, category_name, status, created_at FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching category by ID: " + categoryId, e);
        }
        return null;
    }

    /**
     * Checks if a category name already exists (excluding a specific categoryId when updating).
     */
    public boolean isCategoryNameExists(String name, int excludeId) {
        String sql = "SELECT 1 FROM categories WHERE LOWER(category_name) = LOWER(?) AND category_id != ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking category name existence: " + name, e);
        }
        return false;
    }

    /**
     * Retrieves all categories (active & inactive) for admin management.
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, status, created_at FROM categories ORDER BY category_name ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all categories", e);
        }
        return list;
    }

    /**
     * Retrieves only ACTIVE categories for customer catalog browsing.
     */
    public List<Category> getActiveCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, status, created_at FROM categories WHERE status = 'ACTIVE' ORDER BY category_name ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active categories", e);
        }
        return list;
    }

    /**
     * Checks whether any products exist under this category.
     */
    public boolean hasProducts(int categoryId) {
        String sql = "SELECT 1 FROM products WHERE category_id = ? LIMIT 1";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking products for category: " + categoryId, e);
        }
        return false;
    }

    /**
     * Counts total categories for admin dashboard.
     */
    public int countTotalCategories() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting categories", e);
        }
        return 0;
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
