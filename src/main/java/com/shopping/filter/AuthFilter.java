package com.shopping.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication and Authorization Security Filter.
 * Protects:
 * 1. Customer-only routes (/cart, /checkout, /my-orders, /order-detail, /profile, /submit-review)
 * 2. Admin-only routes (/admin/* excluding /admin/login)
 * Also sets HTTP cache prevention headers on protected pages.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        HttpSession session = request.getSession(false);

        // Prevent back-button browser cache on authenticated pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // 1. Admin route protection
        if (path.startsWith("/admin")) {
            boolean isLoginPage = path.equals("/admin/login") || path.equals("/admin/login.jsp");
            boolean isAdminLoggedIn = (session != null && session.getAttribute("admin") != null);

            if (!isLoginPage && !isAdminLoggedIn) {
                response.sendRedirect(contextPath + "/admin/login?error=Please login to access admin portal");
                return;
            }
            if (isLoginPage && isAdminLoggedIn) {
                response.sendRedirect(contextPath + "/admin/dashboard");
                return;
            }
        }

        // 2. Customer protected routes
        if (isCustomerProtectedRoute(path)) {
            boolean isUserLoggedIn = (session != null && session.getAttribute("user") != null);
            if (!isUserLoggedIn) {
                // Save redirect destination
                String query = request.getQueryString();
                String target = path + (query != null ? "?" + query : "");
                if (session == null) {
                    session = request.getSession(true);
                }
                session.setAttribute("redirectAfterLogin", target);
                response.sendRedirect(contextPath + "/login?message=Please login to continue");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private boolean isCustomerProtectedRoute(String path) {
        return path.startsWith("/checkout") ||
               path.startsWith("/my-orders") ||
               path.startsWith("/order-detail") ||
               path.startsWith("/order-confirmation") ||
               path.startsWith("/profile") ||
               path.startsWith("/submit-review");
    }

    @Override
    public void destroy() {
    }
}
