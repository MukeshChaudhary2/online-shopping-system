package com.shopping.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Automatically initializes database schema and sample seed data on cloud deployments
 * (Render, Railway, AWS, Docker) if database tables do not exist yet.
 */
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    private DatabaseInitializer() {
    }

    /**
     * Checks if the target database contains tables (specifically 'users').
     * If not, automatically runs schema.sql followed by sample_data.sql.
     */
    public static synchronized void initializeDatabaseIfEmpty() {
        LOGGER.info("Checking database schema status...");
        try (Connection conn = DBConnectionUtil.getConnection()) {
            if (isDatabasePopulated(conn)) {
                LOGGER.info("Database tables already exist. Skipping automatic schema initialization.");
                return;
            }

            LOGGER.info("No existing tables detected. Starting automatic database provisioning...");
            executeSqlScript(conn, "database/schema.sql");
            executeSqlScript(conn, "database/sample_data.sql");
            LOGGER.info("Database provisioning completed successfully: schema and initial data loaded.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database auto-initialization warning: " + e.getMessage(), e);
        }
    }

    private static boolean isDatabasePopulated(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(conn.getCatalog(), null, "users", new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true;
                }
            }
            // Fallback direct check in case getTables is restricted or case-sensitive
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 1 FROM users LIMIT 1")) {
                return true;
            } catch (Exception ignored) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void executeSqlScript(Connection conn, String resourcePath) {
        LOGGER.info("Executing SQL resource: " + resourcePath);
        InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            LOGGER.warning("SQL resource not found in classpath: " + resourcePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip standalone line comments
                if (trimmed.startsWith("--") || trimmed.startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
            }

            String fullSql = sb.toString();
            // Split individual SQL statements by semicolon
            String[] statements = fullSql.split(";");
            try (Statement st = conn.createStatement()) {
                for (String rawSql : statements) {
                    String sql = cleanSqlStatement(rawSql);
                    if (sql.isEmpty()) {
                        continue;
                    }

                    // Skip database creation and database switching on shared/cloud databases (TiDB, Render, etc.)
                    String upper = sql.toUpperCase();
                    if (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ")) {
                        LOGGER.fine("Skipping database management statement on cloud DB: " + sql);
                        continue;
                    }

                    try {
                        st.execute(sql);
                    } catch (Exception ex) {
                        LOGGER.warning("Notice on executing SQL (" + ex.getMessage() + ") for: " +
                                (sql.length() > 60 ? sql.substring(0, 60) + "..." : sql));
                    }
                }
            }
            LOGGER.info("Completed execution of " + resourcePath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed executing SQL script " + resourcePath + ": " + e.getMessage(), e);
        }
    }

    private static String cleanSqlStatement(String sql) {
        if (sql == null) return "";
        String s = sql.trim();
        // Remove block comments /* ... */
        s = s.replaceAll("/\\*.*?\\*/", "");
        return s.trim();
    }
}
