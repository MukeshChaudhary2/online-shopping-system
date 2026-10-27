package com.shopping.servlet;

import com.google.gson.Gson;
import com.shopping.dao.CartDAO;
import com.shopping.dao.ProductDAO;
import com.shopping.model.CartItem;
import com.shopping.model.Product;
import com.shopping.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling Shopping Cart operations:
 * - View cart
 * - Add item (supports standard form submit and asynchronous AJAX/fetch)
 * - Update quantity
 * - Remove line item
 * - Live item count JSON endpoint for navbar badge
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/count"})
public class CartServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 1. Live badge count endpoint
        if ("/cart/count".equals(path)) {
            handleCartCountAjax(request, response);
            return;
        }

        // 2. Remove line item via GET link fallback
        if ("/cart/remove".equals(path)) {
            handleRemove(request, response);
            return;
        }

        // 3. View cart page
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?message=Please login to view your cart");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<CartItem> cartItems = cartDAO.getCartByUserId(user.getUserId());
        BigDecimal cartTotal = cartDAO.getCartTotal(user.getUserId());

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);

        request.getRequestDispatcher("/WEB-INF/views/customer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/cart/add".equals(path)) {
            handleAdd(request, response);
        } else if ("/cart/update".equals(path)) {
            handleUpdate(request, response);
        } else if ("/cart/remove".equals(path)) {
            handleRemove(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) ||
                         request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            if (isAjax) {
                sendJsonResponse(response, false, "Please login first to add items to your cart.", 0);
            } else {
                response.sendRedirect(request.getContextPath() + "/login?message=Please login to add items to cart");
            }
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = 1;
            String qtyParam = request.getParameter("quantity");
            if (qtyParam != null && !qtyParam.trim().isEmpty()) {
                quantity = Math.max(1, Integer.parseInt(qtyParam.trim()));
            }

            // Verify stock
            Product product = productDAO.getProductById(productId);
            if (product == null || !"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                if (isAjax) {
                    sendJsonResponse(response, false, "Product is currently unavailable.", 0);
                } else {
                    response.sendRedirect(request.getContextPath() + "/products?error=Product not available");
                }
                return;
            }

            if (product.getStock() < quantity) {
                if (isAjax) {
                    sendJsonResponse(response, false, "Only " + product.getStock() + " units available in stock.", 0);
                } else {
                    response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&error=Insufficient stock");
                }
                return;
            }

            boolean success = cartDAO.addToCart(user.getUserId(), productId, quantity);
            int newCartCount = cartDAO.getCartItemCount(user.getUserId());

            if (isAjax) {
                sendJsonResponse(response, success, success ? "Added to cart successfully!" : "Failed to add to cart.", newCartCount);
            } else {
                response.sendRedirect(request.getContextPath() + "/cart?success=Item added to cart");
            }

        } catch (NumberFormatException e) {
            if (isAjax) {
                sendJsonResponse(response, false, "Invalid product or quantity parameter.", 0);
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            cartDAO.updateQuantity(cartId, user.getUserId(), quantity);
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            cartDAO.removeFromCart(cartId, user.getUserId());
            response.sendRedirect(request.getContextPath() + "/cart?success=Item removed from cart");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleCartCountAjax(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int count = 0;
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            count = cartDAO.getCartItemCount(user.getUserId());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("count", count);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(map));
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int cartCount)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", message);
        data.put("cartCount", cartCount);
        response.getWriter().write(gson.toJson(data));
    }
}
