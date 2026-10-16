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
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for product browsing, searching, and filtering.
 */
@WebServlet(name = "ProductCatalogServlet", urlPatterns = {"/products"})
public class ProductCatalogServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String categoryIdParam = request.getParameter("category");
        String minPriceParam = request.getParameter("minPrice");
        String maxPriceParam = request.getParameter("maxPrice");
        String sort = request.getParameter("sort");

        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdParam.trim());
            } catch (NumberFormatException ignored) {}
        }

        BigDecimal minPrice = null;
        if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
            try {
                minPrice = new BigDecimal(minPriceParam.trim());
            } catch (NumberFormatException ignored) {}
        }

        BigDecimal maxPrice = null;
        if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
            try {
                maxPrice = new BigDecimal(maxPriceParam.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Product> products = productDAO.searchProducts(keyword, categoryId, minPrice, maxPrice, sort);
        List<Category> categories = categoryDAO.getActiveCategories();

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPriceParam);
        request.setAttribute("maxPrice", maxPriceParam);
        request.setAttribute("sort", sort);

        request.getRequestDispatcher("/WEB-INF/views/customer/products.jsp").forward(request, response);
    }
}
