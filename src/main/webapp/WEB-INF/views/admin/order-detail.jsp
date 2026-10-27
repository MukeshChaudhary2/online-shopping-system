<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Manage Order #${order.orderId} | ShopHub Admin" />
<c:set var="activePage" value="orders" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Order #${order.orderId} Fulfilment</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">
            Placed on <fmt:formatDate value="${order.orderDate}" pattern="dd MMM yyyy, hh:mm a"/>
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline btn-sm">&larr; Back to All Orders</a>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; align-items: start;">

    <!-- Order Items & Invoice Details -->
    <div>
        <div class="admin-card">
            <h3 class="admin-card-title" style="margin-bottom: 1.2rem;">Ordered Products (${order.items.size()})</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Price</th>
                        <th>Qty</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${order.items}">
                        <tr>
                            <td>
                                <div style="display: flex; align-items: center; gap: 0.8rem;">
                                    <img src="${pageContext.request.contextPath}/uploads/${item.productImage}" alt="" class="thumb-small"
                                         onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                                    <strong><c:out value="${item.productName}"/></strong>
                                </div>
                            </td>
                            <td>₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                            <td>${item.quantity}</td>
                            <td style="font-weight: 700;">₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="3" style="text-align: right; font-weight: 700;">Grand Total:</td>
                        <td style="font-weight: 800; font-size: 1.1rem; color: var(--primary);">
                            ₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Customer & Shipping Information -->
        <div class="admin-card">
            <h3 class="admin-card-title" style="margin-bottom: 1rem;">Customer & Shipping Address</h3>
            <p style="margin-bottom: 0.5rem;">
                <strong>Name:</strong> <c:out value="${order.userName}"/><br>
                <strong>Email:</strong> <c:out value="${order.userEmail}"/><br>
                <strong>Phone:</strong> <c:out value="${order.userPhone}"/>
            </p>
            <p style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 1rem; color: var(--secondary); white-space: pre-line;">
                <c:out value="${order.shippingAddress}"/>
            </p>
        </div>
    </div>

    <!-- Right Controls: Status & Logistics -->
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">

        <!-- Order Status Update Form -->
        <div class="admin-card">
            <h3 class="admin-card-title" style="margin-bottom: 1rem;">Update Order Status</h3>
            <form action="${pageContext.request.contextPath}/admin/orders/update-status" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="orderId" value="${order.orderId}">

                <div class="form-group">
                    <label class="form-label">Current Status</label>
                    <select name="orderStatus" class="form-control" style="font-weight: 600;">
                        <option value="PENDING" ${order.orderStatus == 'PENDING' ? 'selected' : ''}>PENDING</option>
                        <option value="PROCESSING" ${order.orderStatus == 'PROCESSING' ? 'selected' : ''}>PROCESSING</option>
                        <option value="SHIPPED" ${order.orderStatus == 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                        <option value="DELIVERED" ${order.orderStatus == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                        <option value="CANCELLED" ${order.orderStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED (Restores Stock)</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Update Status</button>
            </form>
        </div>

        <!-- Courier & Logistics Update Form -->
        <div class="admin-card">
            <h3 class="admin-card-title" style="margin-bottom: 1rem;">Courier Tracking Logistics</h3>
            <form action="${pageContext.request.contextPath}/admin/orders/update-tracking" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="orderId" value="${order.orderId}">

                <div class="form-group">
                    <label class="form-label">Courier Company *</label>
                    <input type="text" name="courierName" class="form-control" required
                           value="<c:out value='${not empty order.trackingDetail ? order.trackingDetail.courierName : \"BlueDart Express\"}'/>">
                </div>

                <div class="form-group">
                    <label class="form-label">Waybill / Tracking No *</label>
                    <input type="text" name="trackingNo" class="form-control" required
                           value="<c:out value='${not empty order.trackingDetail ? order.trackingDetail.trackingNo : \"\"}'/>">
                </div>

                <div class="form-group">
                    <label class="form-label">Delivery Stage</label>
                    <select name="deliveryStatus" class="form-control">
                        <c:set var="ds" value="${not empty order.trackingDetail ? order.trackingDetail.deliveryStatus : 'ORDER_PLACED'}" />
                        <option value="ORDER_PLACED" ${ds == 'ORDER_PLACED' ? 'selected' : ''}>ORDER_PLACED</option>
                        <option value="DISPATCHED" ${ds == 'DISPATCHED' ? 'selected' : ''}>DISPATCHED</option>
                        <option value="IN_TRANSIT" ${ds == 'IN_TRANSIT' ? 'selected' : ''}>IN_TRANSIT</option>
                        <option value="OUT_FOR_DELIVERY" ${ds == 'OUT_FOR_DELIVERY' ? 'selected' : ''}>OUT_FOR_DELIVERY</option>
                        <option value="DELIVERED" ${ds == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-outline btn-block" style="border-color: var(--primary); color: var(--primary);">
                    Save Tracking Info
                </button>
            </form>
        </div>

        <!-- Payment Status Record -->
        <div class="admin-card">
            <h3 class="admin-card-title" style="margin-bottom: 0.8rem;">Payment Record</h3>
            <div class="summary-row">
                <span>Mode:</span>
                <strong><c:out value="${order.payment.paymentMode}"/></strong>
            </div>
            <div class="summary-row">
                <span>Status:</span>
                <span class="badge ${order.payment.paymentStatus == 'COMPLETED' ? 'badge-success' : 'badge-warning'}">
                    <c:out value="${order.payment.paymentStatus}"/>
                </span>
            </div>
            <c:if test="${not empty order.payment.transactionId}">
                <div class="summary-row">
                    <span>Txn ID:</span>
                    <code style="font-size: 0.8rem;"><c:out value="${order.payment.transactionId}"/></code>
                </div>
            </c:if>
        </div>

    </div>

</div>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
