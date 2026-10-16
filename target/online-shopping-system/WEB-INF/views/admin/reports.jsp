<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Management Reports | ShopHub Admin" />
<c:set var="activePage" value="reports" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Business Analytics & Reports</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Analyze orders, sales trends, stock shortages, and customer lifetime values.</p>
    </div>
</div>

<!-- Tab Navigation Bar -->
<div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem; border-bottom: 2px solid var(--border); padding-bottom: 0.5rem; overflow-x: auto;">
    <a href="${pageContext.request.contextPath}/admin/reports?type=orders"
       class="btn ${reportType == 'orders' ? 'btn-primary' : 'btn-outline'} btn-sm">
        Orders Report
    </a>
    <a href="${pageContext.request.contextPath}/admin/reports?type=sales_daily"
       class="btn ${reportType == 'sales_daily' ? 'btn-primary' : 'btn-outline'} btn-sm">
        Daily Sales
    </a>
    <a href="${pageContext.request.contextPath}/admin/reports?type=sales_monthly"
       class="btn ${reportType == 'sales_monthly' ? 'btn-primary' : 'btn-outline'} btn-sm">
        Monthly Sales
    </a>
    <a href="${pageContext.request.contextPath}/admin/reports?type=low_stock"
       class="btn ${reportType == 'low_stock' ? 'btn-primary' : 'btn-outline'} btn-sm">
        Low-Stock Warning
    </a>
    <a href="${pageContext.request.contextPath}/admin/reports?type=customers"
       class="btn ${reportType == 'customers' ? 'btn-primary' : 'btn-outline'} btn-sm">
        Customer Analytics
    </a>
</div>

<!-- Report Content Body -->
<div class="admin-card">

    <!-- 1. Orders Report -->
    <c:if test="${reportType == 'orders'}">
        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <form action="${pageContext.request.contextPath}/admin/reports" method="get" style="display: flex; gap: 0.8rem; align-items: flex-end; flex-wrap: wrap;">
                <input type="hidden" name="type" value="orders">

                <div>
                    <label style="font-size: 0.8rem; font-weight: 600; display: block;">From Date</label>
                    <input type="date" name="startDate" value="<c:out value='${startDate}'/>" class="form-control" style="padding: 0.4rem 0.6rem;">
                </div>

                <div>
                    <label style="font-size: 0.8rem; font-weight: 600; display: block;">To Date</label>
                    <input type="date" name="endDate" value="<c:out value='${endDate}'/>" class="form-control" style="padding: 0.4rem 0.6rem;">
                </div>

                <div>
                    <label style="font-size: 0.8rem; font-weight: 600; display: block;">Status</label>
                    <select name="status" class="form-control" style="padding: 0.4rem 0.6rem;">
                        <option value="ALL">All Statuses</option>
                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                        <option value="PROCESSING" ${status == 'PROCESSING' ? 'selected' : ''}>PROCESSING</option>
                        <option value="SHIPPED" ${status == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                        <option value="DELIVERED" ${status == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                        <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary btn-sm">Filter</button>
            </form>

            <a href="${pageContext.request.contextPath}/admin/reports/export?type=orders&startDate=${startDate}&endDate=${endDate}&status=${status}"
               class="btn btn-outline btn-sm" style="border-color: var(--success); color: var(--success);">
                ⬇ Export to CSV
            </a>
        </div>

        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Order #</th>
                        <th>Customer</th>
                        <th>Date</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Destination</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${reportData}">
                        <tr>
                            <td><strong>#${o.orderId}</strong></td>
                            <td><c:out value="${o.userName}"/> (<c:out value="${o.userEmail}"/>)</td>
                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                            <td style="font-weight: 700;">₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                            <td><span class="badge badge-info"><c:out value="${o.orderStatus}"/></span></td>
                            <td style="max-width: 250px; font-size: 0.85rem;"><c:out value="${o.shippingAddress}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- 2. Daily Sales Report -->
    <c:if test="${reportType == 'sales_daily'}">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h3 class="admin-card-title">Daily Sales Aggregations (Last 30 Days)</h3>
            <a href="${pageContext.request.contextPath}/admin/reports/export?type=sales_daily"
               class="btn btn-outline btn-sm" style="border-color: var(--success); color: var(--success);">
                ⬇ Export to CSV
            </a>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Total Orders Placed</th>
                        <th>Daily Revenue</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${reportData}">
                        <tr>
                            <td><strong><c:out value="${row.reportDate}"/></strong></td>
                            <td>${row.totalOrders} order(s)</td>
                            <td style="font-weight: 700; color: var(--success);">₹<fmt:formatNumber value="${row.totalRevenue}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- 3. Monthly Sales Report -->
    <c:if test="${reportType == 'sales_monthly'}">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h3 class="admin-card-title">Monthly Revenue Aggregations (Last 12 Months)</h3>
            <a href="${pageContext.request.contextPath}/admin/reports/export?type=sales_monthly"
               class="btn btn-outline btn-sm" style="border-color: var(--success); color: var(--success);">
                ⬇ Export to CSV
            </a>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Month (YYYY-MM)</th>
                        <th>Total Orders Placed</th>
                        <th>Monthly Revenue</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${reportData}">
                        <tr>
                            <td><strong><c:out value="${row.reportMonth}"/></strong></td>
                            <td>${row.totalOrders} order(s)</td>
                            <td style="font-weight: 700; color: var(--success);">₹<fmt:formatNumber value="${row.totalRevenue}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- 4. Low-Stock Report -->
    <c:if test="${reportType == 'low_stock'}">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <form action="${pageContext.request.contextPath}/admin/reports" method="get" style="display: flex; gap: 0.6rem; align-items: center;">
                <input type="hidden" name="type" value="low_stock">
                <label style="font-size: 0.85rem; font-weight: 600;">Stock Threshold &le;</label>
                <input type="number" name="threshold" value="${threshold}" min="1" max="100" class="form-control" style="width: 80px; padding: 0.4rem;">
                <button type="submit" class="btn btn-primary btn-sm">Update Threshold</button>
            </form>

            <a href="${pageContext.request.contextPath}/admin/reports/export?type=low_stock&threshold=${threshold}"
               class="btn btn-outline btn-sm" style="border-color: var(--success); color: var(--success);">
                ⬇ Export to CSV
            </a>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Product ID</th>
                        <th>Product Name</th>
                        <th>Category</th>
                        <th>Unit Price</th>
                        <th>Current Stock</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${reportData}">
                        <tr>
                            <td>#${p.productId}</td>
                            <td><strong><c:out value="${p.productName}"/></strong></td>
                            <td><c:out value="${p.categoryName}"/></td>
                            <td>₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                            <td><span class="badge badge-danger">${p.stock} units left</span></td>
                            <td><c:out value="${p.status}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- 5. Customer Report -->
    <c:if test="${reportType == 'customers'}">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
            <h3 class="admin-card-title">Customer Lifetime Value & Ordering Volume</h3>
            <a href="${pageContext.request.contextPath}/admin/reports/export?type=customers"
               class="btn btn-outline btn-sm" style="border-color: var(--success); color: var(--success);">
                ⬇ Export to CSV
            </a>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Customer Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Registered Date</th>
                        <th>Total Orders</th>
                        <th>Lifetime Spend</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="c" items="${reportData}">
                        <tr>
                            <td>#${c.userId}</td>
                            <td><strong><c:out value="${c.name}"/></strong></td>
                            <td><c:out value="${c.email}"/></td>
                            <td><c:out value="${c.phone}"/></td>
                            <td><fmt:formatDate value="${c.registeredAt}" pattern="dd MMM yyyy"/></td>
                            <td>${c.totalOrders} order(s)</td>
                            <td style="font-weight: 700; color: var(--primary);">₹<fmt:formatNumber value="${c.totalSpent}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

</div>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
