package com.shopping.servlet;

import com.shopping.dao.OrderDAO;
import com.shopping.model.Order;
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
 * Controller handling customer order confirmation, order history, and shipment tracking views.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/my-orders", "/order-detail", "/order-confirmation"})
public class OrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String path = request.getServletPath();

        if ("/order-confirmation".equals(path)) {
            handleOrderConfirmation(request, response, user);
        } else if ("/order-detail".equals(path)) {
            handleOrderDetail(request, response, user);
        } else {
            handleMyOrders(request, response, user);
        }
    }

    private void handleOrderConfirmation(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        String idParam = request.getParameter("orderId");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam.trim());
            Order order = orderDAO.getOrderById(orderId);

            if (order == null || order.getUserId() != user.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/my-orders");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/customer/order-confirmation.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-orders");
        }
    }

    private void handleMyOrders(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        List<Order> orders = orderDAO.getOrdersByUserId(user.getUserId());
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/customer/my-orders.jsp").forward(request, response);
    }

    private void handleOrderDetail(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/my-orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam.trim());
            Order order = orderDAO.getOrderById(orderId);

            // Access control check: user must own this order
            if (order == null || order.getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access to order details.");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/customer/order-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/my-orders");
        }
    }
}
