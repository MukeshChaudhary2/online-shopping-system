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
 * Controller for Customer Profile viewing, address updates, and password modification.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile", "/profile/change-password"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        User freshUser = userDAO.getUserById(sessionUser.getUserId());
        if (freshUser != null) {
            session.setAttribute("user", freshUser);
        }

        CSRFUtil.getToken(session);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        User user = (User) session.getAttribute("user");
        String path = request.getServletPath();

        if ("/profile/change-password".equals(path)) {
            handleChangePassword(request, response, user);
        } else {
            handleUpdateProfile(request, response, user);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        String name = ValidationUtil.sanitize(request.getParameter("name"));
        String phone = ValidationUtil.sanitize(request.getParameter("phone"));
        String address = ValidationUtil.sanitize(request.getParameter("address"));

        if (!ValidationUtil.isNotEmpty(name) || !ValidationUtil.isValidPhone(phone) || !ValidationUtil.isNotEmpty(address)) {
            request.setAttribute("profileError", "Please provide a valid name, phone number, and address.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
            return;
        }

        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);

        boolean updated = userDAO.updateUserProfile(user);
        if (updated) {
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/profile?success=Profile updated successfully!");
        } else {
            request.setAttribute("profileError", "Failed to update profile. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        User dbUser = userDAO.getUserById(user.getUserId());
        if (dbUser == null || !PasswordUtil.checkPassword(currentPassword, dbUser.getPassword())) {
            request.setAttribute("passwordError", "Current password does not match our records.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            request.setAttribute("passwordError", "New password must be at least 8 characters long.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            request.setAttribute("passwordError", "New passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
            return;
        }

        String hashed = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(user.getUserId(), hashed);

        if (updated) {
            response.sendRedirect(request.getContextPath() + "/profile?success=Password changed successfully!");
        } else {
            request.setAttribute("passwordError", "Failed to change password. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }
}
