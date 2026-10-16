<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Secure Checkout | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 1.8rem;">Checkout & Place Order</h1>

<div class="checkout-layout">

    <!-- Shipping and Payment Form -->
    <div class="cart-table-card">
        <form action="${pageContext.request.contextPath}/checkout/place-order" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
                1. Delivery Shipping Address
            </h3>

            <div class="form-group">
                <label class="form-label">Full Street Address & Landmark *</label>
                <textarea name="shippingAddress" class="form-control" rows="4" required placeholder="House/Flat No., Street, Landmark, City, State, PIN Code"><c:out value="${defaultAddress}"/></textarea>
                <small style="color: var(--text-muted); font-size: 0.8rem;">We will dispatch your order via express courier to this address.</small>
            </div>

            <h3 style="font-size: 1.25rem; font-weight: 700; margin: 2rem 0 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
                2. Select Payment Mode
            </h3>

            <div style="display: flex; flex-direction: column; gap: 0.8rem; margin-bottom: 2rem;">
                <label style="display: flex; align-items: center; gap: 0.8rem; padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius-sm); cursor: pointer; background: #f8fafc;">
                    <input type="radio" name="paymentMode" value="COD" checked>
                    <div>
                        <strong>Cash on Delivery (COD)</strong>
                        <div style="font-size: 0.85rem; color: var(--text-muted);">Pay in cash or UPI when your shipment arrives at your doorstep.</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 0.8rem; padding: 1rem; border: 1px solid var(--border); border-radius: var(--radius-sm); cursor: pointer; background: #f8fafc;">
                    <input type="radio" name="paymentMode" value="ONLINE">
                    <div>
                        <strong>Instant Online Payment (Simulated Gateway)</strong>
                        <div style="font-size: 0.85rem; color: var(--text-muted);">UPI / Credit / Debit Cards / NetBanking (Instantly marked completed).</div>
                    </div>
                </label>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">
                Confirm & Place Order (₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/>)
            </button>
        </form>
    </div>

    <!-- Order Review Summary -->
    <div class="summary-card">
        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.3rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
            Order Items (${cartItems.size()})
        </h3>

        <div style="display: flex; flex-direction: column; gap: 0.8rem; margin-bottom: 1.5rem;">
            <c:forEach var="item" items="${cartItems}">
                <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.9rem;">
                    <div style="display: flex; align-items: center; gap: 0.6rem;">
                        <img src="${pageContext.request.contextPath}/uploads/${item.productImage}" alt=""
                             style="width: 40px; height: 40px; object-fit: contain; background: var(--bg-light); border-radius: 4px;"
                             onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                        <div>
                            <div style="font-weight: 600;"><c:out value="${item.productName}"/></div>
                            <div style="color: var(--text-muted); font-size: 0.8rem;">Qty: ${item.quantity} &times; ₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00"/></div>
                        </div>
                    </div>
                    <strong>₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></strong>
                </div>
            </c:forEach>
        </div>

        <div class="summary-row">
            <span>Subtotal</span>
            <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
        </div>
        <div class="summary-row">
            <span>Shipping</span>
            <span style="color: var(--success); font-weight: 600;">FREE</span>
        </div>

        <div class="summary-row summary-total">
            <span>Payable Amount</span>
            <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
        </div>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
