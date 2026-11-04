<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Order Confirmed | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 3.5rem 2rem; max-width: 680px; margin: 2rem auto; text-align: center; box-shadow: var(--shadow);">
    <div style="width: 72px; height: 72px; background: #dcfce7; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem; color: #16a34a; font-size: 2.2rem;">
        &check;
    </div>

    <h1 style="font-size: 2rem; font-weight: 800; color: var(--text-main); margin-bottom: 0.6rem;">Thank You for Your Order!</h1>
    <p style="color: var(--text-muted); font-size: 1.05rem; margin-bottom: 2rem;">
        Your order <strong>#${order.orderId}</strong> has been placed and is currently being prepared for dispatch.
    </p>

    <!-- Key Order Information Table -->
    <div style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 1.5rem; text-align: left; margin-bottom: 2.5rem;">
        <div class="summary-row">
            <span>Order Number:</span>
            <strong>#${order.orderId}</strong>
        </div>
        <div class="summary-row">
            <span>Order Date:</span>
            <span><fmt:formatDate value="${order.orderDate}" pattern="dd MMM yyyy, hh:mm a"/></span>
        </div>
        <div class="summary-row">
            <span>Total Paid/Payable:</span>
            <strong>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></strong>
        </div>
        <div class="summary-row">
            <span>Payment Mode:</span>
            <span class="badge badge-info"><c:out value="${order.payment.paymentMode}"/> (<c:out value="${order.payment.paymentStatus}"/>)</span>
        </div>
        <c:if test="${not empty order.payment.transactionId}">
            <div class="summary-row">
                <span>Transaction Ref:</span>
                <code><c:out value="${order.payment.transactionId}"/></code>
            </div>
        </c:if>
        <div class="summary-row">
            <span>Tracking Number:</span>
            <span style="font-family: monospace; font-weight: 700; color: var(--primary);"><c:out value="${order.trackingDetail.trackingNo}"/></span>
        </div>
        <div class="summary-row" style="margin-bottom: 0;">
            <span>Delivery Destination:</span>
            <span style="max-width: 350px; text-align: right;"><c:out value="${order.shippingAddress}"/></span>
        </div>
    </div>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="${pageContext.request.contextPath}/order-detail?id=${order.orderId}" class="btn btn-primary">Track Order Status</a>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">Continue Shopping</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
