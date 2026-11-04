package com.shopping.servlet;

import com.shopping.dao.ProductDAO;
import com.shopping.dao.RatingDAO;
import com.shopping.model.Product;
import com.shopping.model.Rating;
import com.shopping.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for individual product detail view and customer review list.
 */
@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final RatingDAO ratingDAO = new RatingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(idParam.trim());
            Product product = productDAO.getProductById(productId);

            if (product == null || !"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found or inactive.");
                return;
            }

            List<Rating> reviews = ratingDAO.getRatingsByProductId(productId);
            double avgRating = ratingDAO.getAverageRating(productId);
            int reviewCount = ratingDAO.getRatingCount(productId);

            // Check review submission eligibility for currently logged in user
            boolean canReview = false;
            boolean alreadyReviewed = false;

            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                canReview = ratingDAO.hasUserPurchasedProduct(user.getUserId(), productId);
                alreadyReviewed = ratingDAO.hasUserRatedProduct(user.getUserId(), productId);
            }

            request.setAttribute("product", product);
            request.setAttribute("reviews", reviews);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("reviewCount", reviewCount);
            request.setAttribute("canReview", canReview);
            request.setAttribute("alreadyReviewed", alreadyReviewed);

            request.getRequestDispatcher("/WEB-INF/views/customer/product-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}
