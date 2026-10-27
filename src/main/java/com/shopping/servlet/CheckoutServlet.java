package com.shopping.servlet;

import com.shopping.dao.CartDAO;
import com.shopping.dao.OrderDAO;
import com.shopping.model.CartItem;
import com.shopping.model.Order;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller managing Checkout and atomic order creation.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/place-order"})
public class CheckoutServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?message=Please login to checkout");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<CartItem> cartItems = cartDAO.getCartByUserId(user.getUserId());

        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=Your cart is empty. Add items before checkout.");
            return;
        }

        BigDecimal cartTotal = cartDAO.getCartTotal(user.getUserId());

        // Ensure CSRF token is initialized
        CSRFUtil.getToken(session);

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartTotal);
        request.setAttribute("defaultAddress", user.getAddress());

        request.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Verify CSRF token
        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String shippingAddress = ValidationUtil.sanitize(request.getParameter("shippingAddress"));
        String paymentMode = ValidationUtil.sanitize(request.getParameter("paymentMode"));

        if (!ValidationUtil.isNotEmpty(shippingAddress)) {
            request.setAttribute("error", "Shipping address is required.");
            doGet(request, response);
            return;
        }

        if (!"COD".equalsIgnoreCase(paymentMode) && !"ONLINE".equalsIgnoreCase(paymentMode)) {
            paymentMode = "COD"; // Default fallback
        }

        List<CartItem> cartItems = cartDAO.getCartByUserId(user.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=Your cart is empty.");
            return;
        }

        BigDecimal totalAmount = cartDAO.getCartTotal(user.getUserId());

        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);

        try {
            int orderId = orderDAO.placeOrderTransaction(order, cartItems, paymentMode, "Standard Express Logistics");
            response.sendRedirect(request.getContextPath() + "/order-confirmation?orderId=" + orderId);

        } catch (IllegalStateException e) {
            // Business rule failure (e.g. stock deficit during checkout)
            LOGGER.log(Level.WARNING, "Checkout business validation failed: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            doGet(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during order placement transaction", e);
            request.setAttribute("error", "An unexpected error occurred while placing your order. Please try again.");
            doGet(request, response);
        }
    }
}
