package com.shopping.servlet;

import com.shopping.dao.AdminDAO;
import com.shopping.model.Admin;
import com.shopping.util.CSRFUtil;
import com.shopping.util.PasswordUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for Administrator Authentication.
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin/login", "/admin/logout"})
public class AdminLoginServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("admin");
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/admin/login?message=Admin logged out successfully.");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        CSRFUtil.getToken(request.getSession(true));
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String username = ValidationUtil.sanitize(request.getParameter("username"));
        String password = request.getParameter("password");

        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
            return;
        }

        Admin admin = adminDAO.getAdminByUsername(username);

        if (admin == null || !PasswordUtil.checkPassword(password, admin.getPassword())) {
            request.setAttribute("error", "Invalid admin credentials.");
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
            return;
        }

        // Session fixation protection
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("admin", admin);
        CSRFUtil.getToken(session);

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
