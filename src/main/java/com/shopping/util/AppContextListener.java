package com.shopping.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * ServletContextListener that oversees application lifecycle events.
 * Safely tests connection on startup and cleans up pool on shutdown.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Online Shopping System web application is starting...");
        try {
            DBConnectionUtil.getConnection().close();
            LOGGER.info("Database connection verified and HikariCP initialized successfully.");
        } catch (Throwable t) {
            LOGGER.warning("Database connection could not be established on startup: " + t.getMessage() +
                    ". Web application is still started; please check MySQL status and db.properties.");
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
