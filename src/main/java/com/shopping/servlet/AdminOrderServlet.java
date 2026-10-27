package com.shopping.servlet;

import com.shopping.dao.OrderDAO;
import com.shopping.dao.PaymentDAO;
import com.shopping.dao.TrackingDAO;
import com.shopping.model.Order;
import com.shopping.util.CSRFUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Admin Order Fulfilment, Status Changes, and Courier Tracking updates.
 */
@WebServlet(name = "AdminOrderServlet", urlPatterns = {
        "/admin/orders",
        "/admin/orders/detail",
        "/admin/orders/update-status",
        "/admin/orders/update-tracking"
})
public class AdminOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final TrackingDAO trackingDAO = new TrackingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/orders/detail".equals(path)) {
            handleOrderDetail(request, response);
            return;
        }

        List<Order> orders = orderDAO.getAllOrders();
        request.setAttribute("orders", orders);
        CSRFUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String path = request.getServletPath();

        if ("/admin/orders/update-status".equals(path)) {
            handleUpdateStatus(request, response);
        } else if ("/admin/orders/update-tracking".equals(path)) {
            handleUpdateTracking(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            Order order = orderDAO.getOrderById(orderId);

            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/admin/orders?error=Order not found.");
                return;
            }

            request.setAttribute("order", order);
            CSRFUtil.getToken(request.getSession(true));
            request.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = ValidationUtil.sanitize(request.getParameter("orderStatus"));

            boolean updated = orderDAO.updateOrderStatus(orderId, status);
            if (updated) {
                // If delivered and was COD, mark payment as completed
                if ("DELIVERED".equalsIgnoreCase(status)) {
                    paymentDAO.updatePaymentStatus(orderId, "COMPLETED", null);
                }
                response.sendRedirect(request.getContextPath() + "/admin/orders/detail?id=" + orderId + "&success=Order status updated.");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/orders/detail?id=" + orderId + "&error=Failed to update status.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleUpdateTracking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String courier = ValidationUtil.sanitize(request.getParameter("courierName"));
            String trackingNo = ValidationUtil.sanitize(request.getParameter("trackingNo"));
            String deliveryStatus = ValidationUtil.sanitize(request.getParameter("deliveryStatus"));

            boolean updated = trackingDAO.updateTracking(orderId, courier, trackingNo, deliveryStatus);
            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/orders/detail?id=" + orderId + "&success=Tracking details updated.");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/orders/detail?id=" + orderId + "&error=Failed to update tracking.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }
}
