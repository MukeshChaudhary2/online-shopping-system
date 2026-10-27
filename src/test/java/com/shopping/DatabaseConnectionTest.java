package com.shopping;

import com.shopping.dao.UserDAO;
import com.shopping.model.User;
import com.shopping.util.DBConnectionUtil;
import com.shopping.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testActualDatabaseConnectionAndTables() {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            System.out.println("SUCCESS: Connected to MySQL database!");

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("RDBMS: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());

            // Check if 'users' table exists
            boolean usersTableExists = false;
            try (ResultSet rs = meta.getTables(null, null, "users", null)) {
                if (rs.next()) {
                    usersTableExists = true;
                    System.out.println("SUCCESS: 'users' table exists in database.");
                }
            }

            assertTrue(usersTableExists, "Table 'users' must exist in shopping_db");

            // Test UserDAO.getUserByEmail
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail("john.doe@example.com");
            if (user != null) {
                System.out.println("SUCCESS: Found user: " + user.getEmail() + " (" + user.getName() + ")");
                assertTrue(PasswordUtil.checkPassword("Customer@123", user.getPassword()));
                System.out.println("SUCCESS: Password verified for john.doe@example.com");
            } else {
                System.out.println("NOTICE: User john.doe@example.com not found. Sample data may not be imported.");
            }

        } catch (SQLException e) {
            System.err.println("DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
            fail("Database connection failed: " + e.getMessage());
        }
    }
}
