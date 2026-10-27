<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Admin Dashboard | ShopHub" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<!-- Dynamic KPI Section (Matching User's Sketch) -->
<div class="dashboard-stats-row">
    <!-- Left Section: 2 Circular Dynamic Gauges -->
    <div class="circle-widgets-box">
        <!-- Circle 1: Total Product -->
        <a href="${pageContext.request.contextPath}/admin/products" class="circle-stat-card circle-product" title="Manage Products">
            <span class="circle-icon">📦</span>
            <span class="circle-title">Total Product</span>
            <span class="circle-count">${totalProducts}</span>
            <span class="circle-sub">In Catalog</span>
        </a>

        <!-- Circle 2: Total Categories -->
        <a href="${pageContext.request.contextPath}/admin/categories" class="circle-stat-card circle-category" title="Manage Categories">
            <span class="circle-icon">🏷️</span>
            <span class="circle-title">Total Categories</span>
            <span class="circle-count">${totalCategories}</span>
            <span class="circle-sub">Active & Inactive</span>
        </a>
    </div>

    <!-- Right Section: Order Status Matrix -->
    <div class="order-matrix-box">
        <!-- Top Row: Total Order (Full Width) -->
        <div class="matrix-row-top">
            <a href="${pageContext.request.contextPath}/admin/orders" class="matrix-card matrix-card-top" title="View All Orders">
                <div>
                    <div class="matrix-label">Total Order</div>
                    <div style="font-size: 0.8rem; color: #94a3b8; font-weight: 500; margin-top: 0.2rem;">All recorded customer orders</div>
                </div>
                <div class="matrix-value">${totalOrders}</div>
            </a>
        </div>

        <!-- Middle Row: 3 Columns (Pending, Processing, Shipped) -->
        <div class="matrix-row-middle">
            <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING" class="matrix-card matrix-pending" title="View Pending Orders">
                <div class="matrix-label">
                    <span>Pending Order</span>
                    <span style="font-size: 1rem;">⏳</span>
                </div>
                <div class="matrix-value">${pendingOrders}</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/orders?status=PROCESSING" class="matrix-card matrix-processing" title="View Processing Orders">
                <div class="matrix-label">
                    <span>Processing</span>
                    <span style="font-size: 1rem;">⚙️</span>
                </div>
                <div class="matrix-value">${processingOrders}</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPED" class="matrix-card matrix-shipped" title="View Shipped Orders">
                <div class="matrix-label">
                    <span>Shipped</span>
                    <span style="font-size: 1rem;">🚚</span>
                </div>
                <div class="matrix-value">${shippedOrders}</div>
            </a>
        </div>

        <!-- Bottom Row: 2 Columns (Delivered, Cancel) -->
        <div class="matrix-row-bottom">
            <a href="${pageContext.request.contextPath}/admin/orders?status=DELIVERED" class="matrix-card matrix-delivered" title="View Delivered Orders">
                <div class="matrix-label">
                    <span>Delivered</span>
                    <span style="font-size: 1rem;">✅</span>
                </div>
                <div class="matrix-value">${deliveredOrders}</div>
            </a>

            <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED" class="matrix-card matrix-cancelled" title="View Cancelled Orders">
                <div class="matrix-label">
                    <span>Cancel</span>
                    <span style="font-size: 1rem;">❌</span>
                </div>
                <div class="matrix-value">${cancelledOrders}</div>
            </a>
        </div>
    </div>
</div>

<!-- Low Stock Alert Banner (if any) -->
<c:if test="${lowStockCount > 0}">
    <div class="alert alert-danger" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <span>⚠️ <strong>Low Stock Warning:</strong> There are <strong>${lowStockCount}</strong> product(s) below safe inventory threshold (5 units).</span>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=low_stock&threshold=5" class="btn btn-danger btn-sm">Inspect Low Stock</a>
    </div>
</c:if>

<!-- ========================================================================= -->
<!-- Bottom Section: Business Analytics & Reports ("niche same hi rakhna")     -->
<!-- ========================================================================= -->
<div style="margin-top: 1.5rem;">
    <div style="margin-bottom: 1.2rem;">
        <h2 style="font-size: 1.5rem; font-weight: 800; color: var(--text-main); margin-bottom: 0.2rem;">Business Analytics & Reports</h2>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Analyze orders, sales trends, stock shortages, and customer lifetime values.</p>
    </div>

    <!-- Tab Navigation Bar -->
    <div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem; border-bottom: 2px solid var(--border); padding-bottom: 0.5rem; overflow-x: auto;">
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=orders"
           class="btn ${reportType == 'orders' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Orders Report
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=sales_daily"
           class="btn ${reportType == 'sales_daily' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Daily Sales
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=sales_monthly"
           class="btn ${reportType == 'sales_monthly' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Monthly Sales
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=low_stock"
           class="btn ${reportType == 'low_stock' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Low-Stock Warning
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=customers"
           class="btn ${reportType == 'customers' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Customer Analytics
        </a>
        <a href="${pageContext.request.contextPath}/admin/dashboard?type=recent"
           class="btn ${reportType == 'recent' ? 'btn-primary' : 'btn-outline'} btn-sm">
            Recent Orders
        </a>
    </div>

    <!-- Dynamic Report / Analytics Card -->
    <div class="admin-card">
        <!-- 1. Orders Report Tab -->
        <c:if test="${reportType == 'orders'}">
            <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" style="display: flex; gap: 0.8rem; align-items: flex-end; flex-wrap: wrap;">
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
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No orders matched the selected filter.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="o" items="${reportData}">
                                    <tr>
                                        <td><strong>#${o.orderId}</strong></td>
                                        <td><c:out value="${o.userName}"/> (<c:out value="${o.userEmail}"/>)</td>
                                        <td><fmt:formatDate value="${o.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                        <td style="font-weight: 700;">₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                                        <td>
                                            <span class="badge ${o.orderStatus == 'DELIVERED' ? 'badge-success' : (o.orderStatus == 'CANCELLED' ? 'badge-danger' : 'badge-info')}">
                                                <c:out value="${o.orderStatus}"/>
                                            </span>
                                        </td>
                                        <td style="max-width: 220px; font-size: 0.85rem;"><c:out value="${o.shippingAddress}"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${o.orderId}" class="btn btn-outline btn-sm">Manage</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>

        <!-- 2. Daily Sales Report Tab -->
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
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="3" style="text-align: center; color: var(--text-muted); padding: 2rem;">No sales data available.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="row" items="${reportData}">
                                    <tr>
                                        <td><strong><c:out value="${row.reportDate}"/></strong></td>
                                        <td>${row.totalOrders} order(s)</td>
                                        <td style="font-weight: 700; color: var(--success);">₹<fmt:formatNumber value="${row.totalRevenue}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>

        <!-- 3. Monthly Sales Report Tab -->
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
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="3" style="text-align: center; color: var(--text-muted); padding: 2rem;">No monthly revenue data available.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="row" items="${reportData}">
                                    <tr>
                                        <td><strong><c:out value="${row.reportMonth}"/></strong></td>
                                        <td>${row.totalOrders} order(s)</td>
                                        <td style="font-weight: 700; color: var(--success);">₹<fmt:formatNumber value="${row.totalRevenue}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>

        <!-- 4. Low-Stock Report Tab -->
        <c:if test="${reportType == 'low_stock'}">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
                <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" style="display: flex; gap: 0.6rem; align-items: center;">
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
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--success); padding: 2rem;">All inventory levels are healthy! No products below threshold.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
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
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>

        <!-- 5. Customer Report Tab -->
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
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No customer records found.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
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
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>

        <!-- 6. Recent Orders Tab -->
        <c:if test="${reportType == 'recent'}">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                <h3 class="admin-card-title">Latest Incoming Orders</h3>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline btn-sm">View All in Orders Manager &rarr;</a>
            </div>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Order #</th>
                            <th>Customer Name</th>
                            <th>Order Date</th>
                            <th>Amount</th>
                            <th>Payment</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty recentOrders}">
                                <tr>
                                    <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No orders placed yet.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="ord" items="${recentOrders}">
                                    <tr>
                                        <td><strong>#${ord.orderId}</strong></td>
                                        <td><c:out value="${ord.userName}"/></td>
                                        <td><fmt:formatDate value="${ord.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                        <td style="font-weight: 700;">₹<fmt:formatNumber value="${ord.totalAmount}" pattern="#,##0.00"/></td>
                                        <td>
                                            <span class="badge ${ord.payment.paymentStatus == 'COMPLETED' ? 'badge-success' : 'badge-warning'}">
                                                <c:out value="${ord.payment.paymentMode}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${ord.orderStatus == 'DELIVERED' ? 'badge-success' : (ord.orderStatus == 'CANCELLED' ? 'badge-danger' : 'badge-info')}">
                                                <c:out value="${ord.orderStatus}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${ord.orderId}" class="btn btn-outline btn-sm">Manage</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
