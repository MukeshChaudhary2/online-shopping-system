package com.shopping.servlet;

import com.shopping.dao.UserDAO;
import com.shopping.model.User;
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
 * Controller for Customer Authentication (Login).
 * Features BCrypt hash verification and session fixation prevention.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Initialize session CSRF token
        CSRFUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String email = ValidationUtil.sanitize(request.getParameter("email"));
        String password = request.getParameter("password");

        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotEmpty(password)) {
            request.setAttribute("error", "Please provide a valid email and password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.getUserByEmail(email);

        if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
            request.setAttribute("error", "Invalid email or password. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp").forward(request, response);
            return;
        }

        // Session fixation protection: invalidate old session and create fresh session
        HttpSession oldSession = request.getSession(false);
        String redirectUrl = null;
        if (oldSession != null) {
            redirectUrl = (String) oldSession.getAttribute("redirectAfterLogin");
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("user", user);
        CSRFUtil.getToken(newSession); // Regenerate CSRF token for new session

        if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
