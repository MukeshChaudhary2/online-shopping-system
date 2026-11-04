package com.shopping.servlet;

import com.shopping.dao.CategoryDAO;
import com.shopping.dao.FeedbackDAO;
import com.shopping.dao.OrderDAO;
import com.shopping.dao.ProductDAO;
import com.shopping.dao.ReportDAO;
import com.shopping.dao.UserDAO;
import com.shopping.model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Administrator Dashboard KPIs, Order Status Matrix, and Real-Time Business Analytics.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Dynamic Catalog Metrics
        int totalProducts = productDAO.countTotalProducts();
        int totalCategories = categoryDAO.countTotalCategories();
        int totalUsers = userDAO.countTotalUsers();
        BigDecimal totalRevenue = orderDAO.getTotalRevenue();
        int totalFeedbacks = feedbackDAO.countFeedbacks();
        int lowStockCount = productDAO.getLowStockProducts(5).size();

        // 2. Dynamic Order Status Metrics (for the dashboard matrix)
        int totalOrders = orderDAO.countTotalOrders();
        int pendingOrders = orderDAO.countOrdersByStatus("PENDING");
        int processingOrders = orderDAO.countOrdersByStatus("PROCESSING");
        int shippedOrders = orderDAO.countOrdersByStatus("SHIPPED");
        int deliveredOrders = orderDAO.countOrdersByStatus("DELIVERED");
        int cancelledOrders = orderDAO.countOrdersByStatus("CANCELLED");

        // Set KPI attributes for top dashboard cards
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalFeedbacks", totalFeedbacks);
        request.setAttribute("lowStockCount", lowStockCount);

        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("processingOrders", processingOrders);
        request.setAttribute("shippedOrders", shippedOrders);
        request.setAttribute("deliveredOrders", deliveredOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);

        // 3. Dynamic Reports & Analytics for bottom section ("niche same hi rakhna")
        String reportType = request.getParameter("type");
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "orders";
        }
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String status = request.getParameter("status");
        int threshold = 10;
        try {
            String thParam = request.getParameter("threshold");
            if (thParam != null && !thParam.trim().isEmpty()) {
                threshold = Integer.parseInt(thParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        request.setAttribute("reportType", reportType);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("status", status);
        request.setAttribute("threshold", threshold);

        switch (reportType) {
            case "sales_daily":
                request.setAttribute("reportData", reportDAO.getDailySalesReport());
                break;
            case "sales_monthly":
                request.setAttribute("reportData", reportDAO.getMonthlySalesReport());
                break;
            case "low_stock":
                request.setAttribute("reportData", reportDAO.getLowStockReport(threshold));
                break;
            case "customers":
                request.setAttribute("reportData", reportDAO.getCustomerReport());
                break;
            case "orders":
            default:
                request.setAttribute("reportData", reportDAO.getOrderReport(startDate, endDate, status));
                break;
        }

        List<Order> recentOrders = orderDAO.getAllOrders().stream().limit(5).collect(Collectors.toList());
        request.setAttribute("recentOrders", recentOrders);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
