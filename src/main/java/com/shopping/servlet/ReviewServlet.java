package com.shopping.servlet;

import com.shopping.dao.RatingDAO;
import com.shopping.model.Rating;
import com.shopping.model.User;
import com.shopping.util.CSRFUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for product review submissions.
 * Strictly verifies that only users who purchased the product can review it.
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/submit-review"})
public class ReviewServlet extends HttpServlet {

    private final RatingDAO ratingDAO = new RatingDAO();

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

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int ratingVal = Integer.parseInt(request.getParameter("rating"));
            String reviewText = ValidationUtil.sanitize(request.getParameter("review"));

            if (ratingVal < 1 || ratingVal > 5) {
                response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&error=Rating must be between 1 and 5 stars");
                return;
            }

            // Verify purchase eligibility
            if (!ratingDAO.hasUserPurchasedProduct(user.getUserId(), productId)) {
                response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&error=Only verified purchasers can leave reviews.");
                return;
            }

            Rating rating = new Rating(user.getUserId(), productId, ratingVal, reviewText);
            boolean success = ratingDAO.addRating(rating);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&success=Review submitted successfully!");
            } else {
                response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&error=Failed to submit review.");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
