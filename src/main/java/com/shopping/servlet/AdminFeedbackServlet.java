package com.shopping.servlet;

import com.shopping.dao.FeedbackDAO;
import com.shopping.model.Feedback;
import com.shopping.util.CSRFUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for reviewing visitor feedback inquiries in Admin Panel.
 */
@WebServlet(name = "AdminFeedbackServlet", urlPatterns = {"/admin/feedbacks", "/admin/feedbacks/delete"})
public class AdminFeedbackServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Feedback> feedbacks = feedbackDAO.getAllFeedbacks();
        request.setAttribute("feedbacks", feedbacks);
        CSRFUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/WEB-INF/views/admin/feedbacks.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            feedbackDAO.deleteFeedback(id);
            response.sendRedirect(request.getContextPath() + "/admin/feedbacks?success=Feedback message removed.");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/feedbacks");
        }
    }
}
