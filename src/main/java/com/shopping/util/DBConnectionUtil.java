package com.shopping.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resilient, high-performance JDBC Connection Pool manager using HikariCP.
 * Configured via classpath resource db.properties with fallback defaults.
 * Uses lazy thread-safe initialization so failure to connect to MySQL on boot
 * does not abort Tomcat web application deployment.
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
                                LOGGER.warning("db.properties not found on classpath, using default settings.");
                            }
                        }

                        HikariConfig config = new HikariConfig();

                        // Check Railway environment variables first (Cloud deployment)
                        String host = System.getenv("MYSQLHOST");
                        String port = System.getenv("MYSQLPORT");
                        String dbName = System.getenv("MYSQLDATABASE");
                        String user = System.getenv("MYSQLUSER");
                        String password = System.getenv("MYSQLPASSWORD");

                        if (host != null && !host.isEmpty()) {
                            if (port == null || port.isEmpty()) port = "3306";
                            if (dbName == null || dbName.isEmpty()) dbName = "railway";
                            if (user == null || user.isEmpty()) user = "root";

                            String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8", 
                                host, port, dbName);

                            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                            config.setJdbcUrl(jdbcUrl);
                            config.setUsername(user);
                            config.setPassword(password != null ? password : "");
                            LOGGER.info("Using Railway environment variables for database connection.");
                        } else {
                            // Fallback to db.properties for local development
                            config.setDriverClassName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
                            config.setJdbcUrl(props.getProperty("db.url", "jdbc:mysql://localhost:3306/shopping_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8"));
                            config.setUsername(props.getProperty("db.username", "root"));
                            config.setPassword(props.getProperty("db.password", "root"));
                            LOGGER.info("Using db.properties configuration for local connection.");
                        }

                        // Connection Pool configurations
                        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikaricp.maximumPoolSize", "15")));
                        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikaricp.minimumIdle", "5")));
                        config.setIdleTimeout(Long.parseLong(props.getProperty("hikaricp.idleTimeout", "30000")));
                        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikaricp.connectionTimeout", "10000")));
                        config.setMaxLifetime(Long.parseLong(props.getProperty("hikaricp.maxLifetime", "1800000")));
                        config.setPoolName(props.getProperty("hikaricp.poolName", "ShoppingHikariPool"));

                        // MySQL performance optimizations
                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                        config.addDataSourceProperty("useServerPrepStmts", "true");

                        dataSource = new HikariDataSource(config);
                        LOGGER.info("HikariCP connection pool initialized successfully.");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database: " + e.getMessage(), e);
                        throw new SQLException("Unable to connect to database. Please ensure MySQL is running and password is correct: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dataSource;
    }

    /**
     * Borrow an active connection from the pool.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
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