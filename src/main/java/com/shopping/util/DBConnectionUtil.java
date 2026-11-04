package com.shopping.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resilient, cloud-ready JDBC Connection Pool manager using HikariCP.
 * Supports Render, TiDB Cloud, Railway, AWS EC2, and local development seamlessly.
 * Configured via Cloud Environment Variables with fallback to db.properties.
 */
public class DBConnectionUtil {

    private static final Logger LOGGER = Logger.getLogger(DBConnectionUtil.class.getName());
    private static volatile HikariDataSource dataSource;
    private static final Object LOCK = new Object();

    private DBConnectionUtil() {
    }

    /**
     * Initializes or returns the existing HikariCP DataSource.
     */
    private static HikariDataSource getDataSource() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (LOCK) {
                if (dataSource == null || dataSource.isClosed()) {
                    try {
                        Properties props = new Properties();
                        try (InputStream input = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                            if (input != null) {
                                props.load(input);
                            } else {
                                LOGGER.warning("db.properties not found on classpath, using defaults.");
                            }
                        }

                        // Resolve Database Connection Details from Environment or Properties
                        DatabaseConfig resolved = resolveConfig(props);

                        LOGGER.info("Configuring HikariCP with JDBC URL: " + sanitizeJdbcUrl(resolved.jdbcUrl) +
                                ", Username: " + resolved.username);

                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
                        config.setJdbcUrl(resolved.jdbcUrl);
                        config.setUsername(resolved.username);
                        config.setPassword(resolved.password);

                        // Connection Pool settings
                        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikaricp.maximumPoolSize", "10")));
                        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikaricp.minimumIdle", "2")));
                        config.setIdleTimeout(Long.parseLong(props.getProperty("hikaricp.idleTimeout", "30000")));
                        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikaricp.connectionTimeout", "15000")));
                        config.setMaxLifetime(Long.parseLong(props.getProperty("hikaricp.maxLifetime", "1800000")));
                        config.setPoolName(props.getProperty("hikaricp.poolName", "ShopHubPool"));

                        // Cloud database stability & performance optimizations
                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                        config.addDataSourceProperty("useServerPrepStmts", "true");
                        config.addDataSourceProperty("connectTimeout", "10000");
                        config.addDataSourceProperty("socketTimeout", "30000");

                        dataSource = new HikariDataSource(config);
                        LOGGER.info("HikariCP connection pool initialized successfully.");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database: " + e.getMessage(), e);
                        throw new SQLException("Unable to connect to database: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dataSource;
    }

    private static class DatabaseConfig {
        String jdbcUrl;
        String username;
        String password;
    }

    /**
     * Intelligently parses environment variables across different cloud platforms (Render, TiDB, Railway, AWS).
     */
    private static DatabaseConfig resolveConfig(Properties props) {
        DatabaseConfig cfg = new DatabaseConfig();

        // 1. Check full connection URLs (DB_URL, DATABASE_URL, MYSQL_URL)
        String rawUrl = getFirstEnv("DB_URL", "DATABASE_URL", "MYSQL_URL");
        String envUser = getFirstEnv("DB_USER", "MYSQLUSER", "TIDB_USER");
        String envPass = getFirstEnv("DB_PASSWORD", "MYSQLPASSWORD", "TIDB_PASSWORD");

        if (rawUrl != null && !rawUrl.trim().isEmpty()) {
            rawUrl = rawUrl.trim();
            // Handle mysql:// standard URI format commonly emitted by cloud dashboards
            if (rawUrl.startsWith("mysql://")) {
                try {
                    URI uri = new URI(rawUrl.replace("mysql://", "http://"));
                    String host = uri.getHost();
                    int port = uri.getPort() != -1 ? uri.getPort() : 3306;
                    String path = uri.getPath();
                    String dbName = (path != null && path.length() > 1) ? path.substring(1) : "shopping_db";
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        if (envUser == null) envUser = parts[0];
                        if (envPass == null) envPass = parts[1];
                    }
                    String query = uri.getQuery();
                    if (query == null || query.isEmpty()) {
                        query = "useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                    }
                    cfg.jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?" + query;
                } catch (Exception e) {
                    // Fallback to direct replacement
                    cfg.jdbcUrl = "jdbc:" + rawUrl;
                }
            } else if (rawUrl.startsWith("jdbc:mysql://")) {
                cfg.jdbcUrl = rawUrl;
            } else {
                cfg.jdbcUrl = "jdbc:mysql://" + rawUrl;
            }
        }

        // 2. Check individual host/port/database environment variables
        if (cfg.jdbcUrl == null) {
            String host = getFirstEnv("DB_HOST", "MYSQLHOST", "TIDB_HOST");
            if (host != null && !host.trim().isEmpty()) {
                String port = getFirstEnv("DB_PORT", "MYSQLPORT", "TIDB_PORT");
                if (port == null || port.trim().isEmpty()) port = "3306";
                String dbName = getFirstEnv("DB_NAME", "MYSQLDATABASE", "TIDB_DATABASE");
                if (dbName == null || dbName.trim().isEmpty()) dbName = "shopping_db";

                boolean isCloud = !host.equalsIgnoreCase("localhost") && !host.equals("127.0.0.1");
                String sslParams = isCloud
                        ? "useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"
                        : "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

                cfg.jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?" + sslParams;
            }
        }

        // 3. Fallback to db.properties for local development
        if (cfg.jdbcUrl == null) {
            cfg.jdbcUrl = props.getProperty("db.url",
                    "jdbc:mysql://localhost:3306/shopping_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
        }
        cfg.username = (envUser != null && !envUser.trim().isEmpty()) ? envUser.trim() :
                props.getProperty("db.username", "root");
        cfg.password = (envPass != null) ? envPass :
                props.getProperty("db.password", "root");

        return cfg;
    }

    private static String getFirstEnv(String... names) {
        for (String name : names) {
            String val = System.getenv(name);
            if (val != null && !val.trim().isEmpty()) {
                return val.trim();
            }
        }
        return null;
    }

    private static String sanitizeJdbcUrl(String url) {
        if (url == null) return "null";
        // Hide password if embedded in query parameters
        return url.replaceAll("(?i)password=[^&;]*", "password=***");
    }

    /**
     * Borrow an active connection from the pool.
     */
    public static Connection getConnection() throws SQLException {
        HikariDataSource ds = getDataSource();
        return ds.getConnection();
    }

    /**
     * Close the HikariCP DataSource on application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP DataSource closed.");
        }
    }

    /**
     * Helper to close AutoCloseable resources cleanly without throwing checked exceptions.
     */
    public static void closeQuietly(AutoCloseable... resources) {
        if (resources != null) {
            for (AutoCloseable res : resources) {
                if (res != null) {
                    try {
                        res.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
