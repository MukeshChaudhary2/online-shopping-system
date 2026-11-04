<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="My Orders | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 1.8rem;">My Order History</h1>

<c:choose>
    <c:when test="${empty orders}">
        <div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 4rem 2rem; text-align: center;">
            <div style="font-size: 3rem; margin-bottom: 1rem;">📦</div>
            <h2 style="font-size: 1.3rem; margin-bottom: 0.5rem;">No orders placed yet</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Your completed purchases will appear here.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Discover Products</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cart-table-card">
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Order Date</th>
                        <th>Items Count</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Courier Tracking</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ord" items="${orders}">
                        <tr>
                            <td><strong>#${ord.orderId}</strong></td>
                            <td><fmt:formatDate value="${ord.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                            <td>${ord.items.size()} item(s)</td>
                            <td style="font-weight: 700;">₹<fmt:formatNumber value="${ord.totalAmount}" pattern="#,##0.00"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${ord.orderStatus == 'DELIVERED'}">
                                        <span class="badge badge-success">Delivered</span>
                                    </c:when>
                                    <c:when test="${ord.orderStatus == 'SHIPPED'}">
                                        <span class="badge badge-info">Shipped</span>
                                    </c:when>
                                    <c:when test="${ord.orderStatus == 'PROCESSING'}">
                                        <span class="badge badge-warning">Processing</span>
                                    </c:when>
                                    <c:when test="${ord.orderStatus == 'CANCELLED'}">
                                        <span class="badge badge-danger">Cancelled</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-warning">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty ord.trackingDetail}">
                                        <span style="font-family: monospace; font-size: 0.85rem;"><c:out value="${ord.trackingDetail.trackingNo}"/></span>
                                        <div style="font-size: 0.75rem; color: var(--text-muted);"><c:out value="${ord.trackingDetail.deliveryStatus}"/></div>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: var(--text-muted); font-size: 0.85rem;">Pending Dispatch</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/order-detail?id=${ord.orderId}" class="btn btn-outline btn-sm">
                                    View Details
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
