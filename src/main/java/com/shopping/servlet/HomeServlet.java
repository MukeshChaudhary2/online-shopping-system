package com.shopping.servlet;

import com.shopping.dao.CategoryDAO;
import com.shopping.dao.ProductDAO;
import com.shopping.model.Category;
import com.shopping.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Storefront Homepage.
 * Loads categories and featured catalog items.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = new ArrayList<>();
        List<Product> featuredProducts = new ArrayList<>();

        try {
            categories = categoryDAO.getActiveCategories();
            featuredProducts = productDAO.getFeaturedProducts(8);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading homepage catalog data: " + e.getMessage(), e);
            request.setAttribute("error", "Database connection notice: " + e.getMessage() + ". Please verify MySQL is running.");
        }

        request.setAttribute("categories", categories);
        request.setAttribute("featuredProducts", featuredProducts);

        request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
    }
}
