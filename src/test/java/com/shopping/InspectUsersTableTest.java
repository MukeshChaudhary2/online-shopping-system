package com.shopping;

import com.shopping.util.DBConnectionUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class InspectUsersTableTest {

    @Test
    public void printUsersTableSchemaAndInsertDirectly() {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            System.out.println("--- Inspecting 'users' table columns ---");
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users LIMIT 1");
                 ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.println("Column " + i + ": " + md.getColumnName(i) + " (" + md.getColumnTypeName(i) + ") Nullable: " + md.isNullable(i));
                }
            }

            System.out.println("\n--- Attempting direct INSERT ---");
            String sql = "INSERT INTO users (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "Test Direct");
                ps.setString(2, "direct_" + System.currentTimeMillis() + "@test.com");
                ps.setString(3, "$2a$12$abcdefghijklmnopqrstuvwx");
                ps.setString(4, "9876543210");
                ps.setString(5, "Test Address");

                int rows = ps.executeUpdate();
                System.out.println("Direct insert rows affected: " + rows);
            } catch (SQLException e) {
                System.err.println("EXACT SQL EXCEPTION ON INSERT: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
