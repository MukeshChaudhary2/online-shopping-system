package com.shopping;

import com.shopping.util.DBConnectionUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetupRunner {

    @Test
    public void setupDatabaseAndVerify() {
        try (Connection conn = DBConnectionUtil.getConnection()) {
            System.out.println("=== 1. Checking existing tables in shopping_db ===");
            List<String> existingTables = getExistingTables(conn);
            System.out.println("Found tables: " + existingTables);

            if (existingTables.isEmpty() || !existingTables.contains("users")) {
                System.out.println("=== 2. Executing schema.sql ===");
                executeSqlFile(conn, Paths.get("database/schema.sql"));

                System.out.println("=== 3. Executing sample_data.sql ===");
                executeSqlFile(conn, Paths.get("database/sample_data.sql"));

                existingTables = getExistingTables(conn);
                System.out.println("=== Tables after setup ===");
                System.out.println(existingTables);
            } else {
                System.out.println("Tables already exist. Count: " + existingTables.size());
            }

            System.out.println("=== 4. Checking admin and user counts ===");
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT count(*) FROM users")) {
                    if (rs.next()) System.out.println("Users count: " + rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT count(*) FROM admins")) {
                    if (rs.next()) System.out.println("Admins count: " + rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT count(*) FROM categories")) {
                    if (rs.next()) System.out.println("Categories count: " + rs.getInt(1));
                }
                try (ResultSet rs = st.executeQuery("SELECT count(*) FROM products")) {
                    if (rs.next()) System.out.println("Products count: " + rs.getInt(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getExistingTables(Connection conn) throws Exception {
        List<String> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }

    private void executeSqlFile(Connection conn, Path path) throws Exception {
        if (!Files.exists(path)) {
            System.err.println("File not found: " + path.toAbsolutePath());
            return;
        }

        String content = Files.readString(path);
        // Split SQL statements separated by semicolon
        String[] statements = content.split(";");
        try (Statement st = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                // Filter out empty lines or comments
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    // check if there is actual sql in multi-line comments
                    String nonComment = removeComments(trimmed).trim();
                    if (nonComment.isEmpty()) continue;
                    trimmed = nonComment;
                }
                try {
                    st.execute(trimmed);
                } catch (Exception ex) {
                    System.err.println("Error executing SQL:\n" + trimmed + "\nReason: " + ex.getMessage());
                }
            }
        }
    }

    private String removeComments(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String line : sql.split("\\r?\\n")) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("--") && !trimmedLine.startsWith("//")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
