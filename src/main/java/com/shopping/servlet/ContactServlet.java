package com.shopping.servlet;

import com.shopping.dao.FeedbackDAO;
import com.shopping.model.Feedback;
import com.shopping.util.CSRFUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller for Customer Contact and Feedback form submissions.
 */
@WebServlet(name = "ContactServlet", urlPatterns = {"/contact"})
public class ContactServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CSRFUtil.getToken(request.getSession(true));
        request.getRequestDispatcher("/WEB-INF/views/customer/contact.jsp").forward(request, response);
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
        String message = ValidationUtil.sanitize(request.getParameter("message"));

        if (!ValidationUtil.isNotEmpty(name) || !ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotEmpty(message)) {
            request.setAttribute("error", "Please fill in all mandatory fields with valid information.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/WEB-INF/views/customer/contact.jsp").forward(request, response);
            return;
        }

        Feedback feedback = new Feedback(name, email, message);
        boolean saved = feedbackDAO.saveFeedback(feedback);

        if (saved) {
            response.sendRedirect(request.getContextPath() + "/contact?success=Thank you! Your feedback has been received.");
        } else {
            request.setAttribute("error", "Could not submit feedback due to a system error. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/customer/contact.jsp").forward(request, response);
        }
    }
}
