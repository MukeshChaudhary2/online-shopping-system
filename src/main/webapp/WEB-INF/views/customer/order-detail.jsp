<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order #${order.orderId} Details | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800;">Order #${order.orderId}</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">
            Placed on <fmt:formatDate value="${order.orderDate}" pattern="dd MMMM yyyy 'at' hh:mm a"/>
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/my-orders" class="btn btn-outline btn-sm">&larr; Back to Orders</a>
</div>

<!-- Shipment Tracking Timeline -->
<div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 2rem; margin-bottom: 2rem; box-shadow: var(--shadow-sm);">
    <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">Delivery Progress</h3>
    <div style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.5rem;">
        Courier: <strong><c:out value="${order.trackingDetail.courierName}"/></strong> &bull;
        Tracking ID: <code style="font-weight: 700; color: var(--primary);"><c:out value="${order.trackingDetail.trackingNo}"/></code>
    </div>

    <c:set var="status" value="${order.trackingDetail.deliveryStatus}" />
    <div class="tracking-timeline">
        <div class="timeline-step completed">
            <div class="timeline-icon">&check;</div>
            <div class="timeline-label">Order Placed</div>
        </div>
        <div class="timeline-step ${status == 'DISPATCHED' or status == 'IN_TRANSIT' or status == 'OUT_FOR_DELIVERY' or status == 'DELIVERED' ? 'completed' : ''}">
            <div class="timeline-icon">📦</div>
            <div class="timeline-label">Dispatched</div>
        </div>
        <div class="timeline-step ${status == 'IN_TRANSIT' or status == 'OUT_FOR_DELIVERY' or status == 'DELIVERED' ? 'completed' : ''}">
            <div class="timeline-icon">✈️</div>
            <div class="timeline-label">In Transit</div>
        </div>
        <div class="timeline-step ${status == 'OUT_FOR_DELIVERY' or status == 'DELIVERED' ? 'completed' : ''}">
            <div class="timeline-icon">🚚</div>
            <div class="timeline-label">Out for Delivery</div>
        </div>
        <div class="timeline-step ${status == 'DELIVERED' ? 'completed' : ''}">
            <div class="timeline-icon">🏠</div>
            <div class="timeline-label">Delivered</div>
        </div>
    </div>
</div>

<div class="cart-layout">
    <!-- Line Items Table -->
    <div class="cart-table-card">
        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
            Purchased Items (${order.items.size()})
        </h3>
        <table class="cart-table">
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${order.items}">
                    <tr>
                        <td>
                            <div class="cart-item-info">
                                <img src="${pageContext.request.contextPath}/uploads/${item.productImage}" alt="" class="cart-item-thumb"
                                     onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                                <div>
                                    <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-main);">
                                        <c:out value="${item.productName}"/>
                                    </a>
                                </div>
                            </div>
                        </td>
                        <td>₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                        <td>${item.quantity}</td>
                        <td style="font-weight: 700;">₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Payment & Shipping Summary -->
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div class="summary-card">
            <h4 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                Delivery Address
            </h4>
            <p style="font-size: 0.95rem; color: var(--secondary); white-space: pre-line;"><c:out value="${order.shippingAddress}"/></p>
        </div>

        <div class="summary-card">
            <h4 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                Payment Summary
            </h4>
            <div class="summary-row">
                <span>Method:</span>
                <strong><c:out value="${order.payment.paymentMode}"/></strong>
            </div>
            <div class="summary-row">
                <span>Payment Status:</span>
                <span class="badge ${order.payment.paymentStatus == 'COMPLETED' ? 'badge-success' : 'badge-warning'}">
                    <c:out value="${order.payment.paymentStatus}"/>
                </span>
            </div>
            <c:if test="${not empty order.payment.transactionId}">
                <div class="summary-row">
                    <span>Transaction ID:</span>
                    <code style="font-size: 0.8rem;"><c:out value="${order.payment.transactionId}"/></code>
                </div>
            </c:if>
            <div class="summary-row summary-total">
                <span>Total Amount:</span>
                <span>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></span>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
