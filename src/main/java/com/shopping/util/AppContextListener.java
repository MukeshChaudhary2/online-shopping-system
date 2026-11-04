package com.shopping.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * ServletContextListener that oversees application lifecycle events.
 * Performs database health check, automatic table migrations, and clean pool shutdown.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());
    private static long startTime;

    public static long getStartTime() {
        return startTime;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        startTime = System.currentTimeMillis();
        LOGGER.info("Online Shopping System web application is starting...");
        try {
            // Check connection
            DBConnectionUtil.getConnection().close();
            LOGGER.info("Database connection verified and HikariCP initialized successfully.");

            // Run auto-initializer if fresh cloud DB
            DatabaseInitializer.initializeDatabaseIfEmpty();
        } catch (Throwable t) {
            LOGGER.warning("Database connection or schema check error on startup: " + t.getMessage() +
                    ". Web application is still started; verify database credentials.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down Online Shopping System...");
        try {
            DBConnectionUtil.shutdown();
        } catch (Throwable t) {
            LOGGER.warning("Error during pool shutdown: " + t.getMessage());
        }
    }
}
