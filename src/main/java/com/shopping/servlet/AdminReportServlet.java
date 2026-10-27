package com.shopping.servlet;

import com.shopping.dao.ReportDAO;
import com.shopping.model.Order;
import com.shopping.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Controller for Administrative Reports and CSV Exports:
 * 1. Order Report
 * 2. Daily Sales Report
 * 3. Monthly Sales Report
 * 4. Low-Stock Alert Report
 * 5. Customer Report
 */
@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/reports", "/admin/reports/export"})
public class AdminReportServlet extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String reportType = request.getParameter("type");
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "orders";
        }

        if ("/admin/reports/export".equals(path)) {
            handleCsvExport(request, response, reportType);
            return;
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

        request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
    }

    private void handleCsvExport(HttpServletRequest request, HttpServletResponse response, String reportType)
            throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        String fileName = "report_" + reportType + "_" + System.currentTimeMillis() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter out = response.getWriter();

        switch (reportType) {
            case "sales_daily": {
                out.println("Date,Total Orders,Total Revenue (INR)");
                List<Map<String, Object>> daily = reportDAO.getDailySalesReport();
                for (Map<String, Object> r : daily) {
                    out.println(String.format("\"%s\",%s,%s",
                            r.get("reportDate"), r.get("totalOrders"), r.get("totalRevenue")));
                }
                break;
            }
            case "sales_monthly": {
                out.println("Month (YYYY-MM),Total Orders,Total Revenue (INR)");
                List<Map<String, Object>> monthly = reportDAO.getMonthlySalesReport();
                for (Map<String, Object> r : monthly) {
                    out.println(String.format("\"%s\",%s,%s",
                            r.get("reportMonth"), r.get("totalOrders"), r.get("totalRevenue")));
                }
                break;
            }
            case "low_stock": {
                int threshold = 10;
                try {
                    String th = request.getParameter("threshold");
                    if (th != null) threshold = Integer.parseInt(th);
                } catch (NumberFormatException ignored) {}

                out.println("Product ID,Product Name,Category,Price (INR),Current Stock,Status");
                List<Product> products = reportDAO.getLowStockReport(threshold);
                for (Product p : products) {
                    out.println(String.format("%d,\"%s\",\"%s\",%s,%d,\"%s\"",
                            p.getProductId(), escapeCsv(p.getProductName()), escapeCsv(p.getCategoryName()),
                            p.getPrice(), p.getStock(), p.getStatus()));
                }
                break;
            }
            case "customers": {
                out.println("User ID,Customer Name,Email,Phone,Registration Date,Total Orders,Total Spent (INR)");
                List<Map<String, Object>> customers = reportDAO.getCustomerReport();
                for (Map<String, Object> c : customers) {
                    out.println(String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",%s,%s",
                            c.get("userId"), escapeCsv(String.valueOf(c.get("name"))),
                            escapeCsv(String.valueOf(c.get("email"))), escapeCsv(String.valueOf(c.get("phone"))),
                            c.get("registeredAt"), c.get("totalOrders"), c.get("totalSpent")));
                }
                break;
            }
            case "orders":
            default: {
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                String status = request.getParameter("status");

                out.println("Order ID,Customer Name,Customer Email,Order Date,Total Amount (INR),Order Status,Shipping Address");
                List<Order> orders = reportDAO.getOrderReport(startDate, endDate, status);
                for (Order o : orders) {
                    out.println(String.format("%d,\"%s\",\"%s\",\"%s\",%s,\"%s\",\"%s\"",
                            o.getOrderId(), escapeCsv(o.getUserName()), escapeCsv(o.getUserEmail()),
                            o.getOrderDate(), o.getTotalAmount(), o.getOrderStatus(),
                            escapeCsv(o.getShippingAddress())));
                }
                break;
            }
        }
        out.flush();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"").replace("\n", " ").replace("\r", " ");
    }
}
