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
 * Controller handling Customer Registration with comprehensive server-side validations.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        CSRFUtil.getToken(request.getSession(true));
        request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String name = ValidationUtil.sanitize(request.getParameter("name"));
        String email = ValidationUtil.sanitize(request.getParameter("email"));
        String phone = ValidationUtil.sanitize(request.getParameter("phone"));
        String address = ValidationUtil.sanitize(request.getParameter("address"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Server-side validations
        String errorMessage = null;
        if (!ValidationUtil.isNotEmpty(name)) {
            errorMessage = "Full Name is required.";
        } else if (!ValidationUtil.isValidEmail(email)) {
            errorMessage = "Please enter a valid email address.";
        } else if (!ValidationUtil.isValidPhone(phone)) {
            errorMessage = "Please enter a valid phone number (10-15 digits).";
        } else if (!ValidationUtil.isNotEmpty(address)) {
            errorMessage = "Delivery address is required.";
        } else if (!ValidationUtil.isValidPassword(password)) {
            errorMessage = "Password must be at least 8 characters long.";
        } else if (password == null || !password.equals(confirmPassword)) {
            errorMessage = "Passwords do not match.";
        } else if (userDAO.isEmailRegistered(email)) {
            errorMessage = "An account with this email address already exists.";
        }

        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
            return;
        }

        // Hash password securely with BCrypt
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(name, email, hashedPassword, phone, address);

        boolean registered = userDAO.registerUser(user);
        if (registered) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/home?success=Welcome to Online Shopping System!");
        } else {
            request.setAttribute("error", "Registration failed due to a system error. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp").forward(request, response);
        }
    }
}
