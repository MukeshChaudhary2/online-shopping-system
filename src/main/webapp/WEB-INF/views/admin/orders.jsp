<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Orders & Tracking | ShopHub Admin" />
<c:set var="activePage" value="orders" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Customer Orders Management</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Track dispatch progress, change order states, and assign courier logistics.</p>
    </div>
</div>

<div class="admin-card">
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Order #</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Amount</th>
                    <th>Payment</th>
                    <th>Order Status</th>
                    <th>Courier Tracking</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty orders}">
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">No orders registered in system.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="ord" items="${orders}">
                            <tr>
                                <td><strong>#${ord.orderId}</strong></td>
                                <td>
                                    <div><strong><c:out value="${ord.userName}"/></strong></div>
                                    <small style="color: var(--text-muted);"><c:out value="${ord.userEmail}"/></small>
                                </td>
                                <td><fmt:formatDate value="${ord.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td style="font-weight: 700;">₹<fmt:formatNumber value="${ord.totalAmount}" pattern="#,##0.00"/></td>
                                <td>
                                    <span class="badge ${ord.payment.paymentStatus == 'COMPLETED' ? 'badge-success' : 'badge-warning'}">
                                        <c:out value="${ord.payment.paymentMode}"/> (<c:out value="${ord.payment.paymentStatus}"/>)
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${ord.orderStatus == 'DELIVERED' ? 'badge-success' : (ord.orderStatus == 'CANCELLED' ? 'badge-danger' : 'badge-info')}">
                                        <c:out value="${ord.orderStatus}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty ord.trackingDetail}">
                                            <div style="font-size: 0.85rem; font-weight: 600;"><c:out value="${ord.trackingDetail.courierName}"/></div>
                                            <code style="font-size: 0.75rem;"><c:out value="${ord.trackingDetail.trackingNo}"/></code>
                                            <div style="font-size: 0.75rem; color: var(--text-muted);"><c:out value="${ord.trackingDetail.deliveryStatus}"/></div>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: var(--text-muted); font-size: 0.85rem;">Unassigned</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${ord.orderId}" class="btn btn-primary btn-sm">
                                        Manage
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
