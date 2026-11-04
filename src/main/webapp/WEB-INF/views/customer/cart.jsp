<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Your Shopping Cart | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 1.8rem;">Shopping Cart</h1>

<c:choose>
    <c:when test="${empty cartItems}">
        <div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 4rem 2rem; text-align: center;">
            <div style="font-size: 3.5rem; margin-bottom: 1rem;">🛒</div>
            <h2 style="font-size: 1.4rem; margin-bottom: 0.6rem;">Your cart is currently empty</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.8rem;">Explore our vast collection and discover products you love.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Start Shopping Now</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cart-layout">

            <!-- Cart Table Card -->
            <div class="cart-table-card">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td>
                                    <div class="cart-item-info">
                                        <img src="${pageContext.request.contextPath}/uploads/${item.productImage}"
                                             alt="<c:out value='${item.productName}'/>" class="cart-item-thumb"
                                             onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                                        <div>
                                            <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 600; color: var(--text-main);">
                                                <c:out value="${item.productName}"/>
                                            </a>
                                            <c:if test="${item.availableStock < 5}">
                                                <div style="color: var(--danger); font-size: 0.8rem;">Only ${item.availableStock} left!</div>
                                            </c:if>
                                        </div>
                                    </div>
                                </td>
                                <td>₹<fmt:formatNumber value="${item.productPrice}" pattern="#,##0.00"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; align-items: center; gap: 0.3rem;">
                                        <input type="hidden" name="cartId" value="${item.cartId}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.availableStock}"
                                               style="width: 60px; padding: 0.35rem; border: 1px solid var(--border); border-radius: var(--radius-sm); text-align: center;">
                                        <button type="submit" class="btn btn-outline btn-sm" title="Update Quantity">&#x21bb;</button>
                                    </form>
                                </td>
                                <td style="font-weight: 700;">
                                    ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                        <input type="hidden" name="cartId" value="${item.cartId}">
                                        <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger);" title="Remove item">
                                            &times;
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div style="margin-top: 1.5rem;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">&larr; Continue Shopping</a>
                </div>
            </div>

            <!-- Order Summary Card -->
            <div class="summary-card">
                <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.3rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
                    Order Summary
                </h3>

                <div class="summary-row">
                    <span>Subtotal</span>
                    <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
                </div>
                <div class="summary-row">
                    <span>Standard Delivery</span>
                    <span style="color: var(--success); font-weight: 600;">FREE</span>
                </div>

                <div class="summary-row summary-total">
                    <span>Grand Total</span>
                    <span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##0.00"/></span>
                </div>

                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
                    Proceed to Checkout &rarr;
                </a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
