package com.shopping.servlet;

import com.shopping.util.AppContextListener;
import com.shopping.util.DBConnectionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * High-performance Health Check & Keep-Alive Servlet.
 * Mapped to /health and /ping.
 * Used by Render Health Check and UptimeRobot (pings every 5 minutes to keep Render alive 24/7 forever).
 */
@WebServlet(urlPatterns = {"/health", "/ping"})
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        boolean dbConnected = false;
        try (Connection conn = DBConnectionUtil.getConnection()) {
            dbConnected = conn.isValid(2);
        } catch (Throwable ignored) {
            // Logged inside pool if critical, avoid throwing in health check
        }

        long uptimeSeconds = (System.currentTimeMillis() - AppContextListener.getStartTime()) / 1000;
        if (uptimeSeconds < 0) uptimeSeconds = 0;

        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\n");
            out.write("  \"status\": \"UP\",\n");
            out.write("  \"application\": \"ShopHub Online Shopping System\",\n");
            out.write("  \"database\": \"" + (dbConnected ? "CONNECTED" : "DISCONNECTED") + "\",\n");
            out.write("  \"uptimeSeconds\": " + uptimeSeconds + ",\n");
            out.write("  \"timestamp\": " + System.currentTimeMillis() + "\n");
            out.write("}\n");
        }
    }
}
