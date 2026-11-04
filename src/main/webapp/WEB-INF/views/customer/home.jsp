<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Home | ShopHub Online Shopping System" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Hero Banner -->
<div class="hero-banner">
    <div class="container hero-content">
        <h1 class="hero-title">Experience Modern Shopping with ShopHub</h1>
        <p class="hero-subtitle">Discover top-quality electronics, fashionable apparel, kitchenware, books, and fitness gear with express doorstep delivery.</p>
        <div style="display: flex; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Browse Full Catalog</a>
            <a href="${pageContext.request.contextPath}/products?sort=price_asc" class="btn btn-outline btn-lg" style="color: white; border-color: rgba(255,255,255,0.3);">Best Deals</a>
        </div>
    </div>
</div>

<!-- Category Showcase -->
<div class="section-header">
    <h2 class="section-title">Shop by Category</h2>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">View All Categories &rarr;</a>
</div>

<div class="category-grid">
    <c:forEach var="cat" items="${categories}">
        <a href="${pageContext.request.contextPath}/products?category=${cat.categoryId}" class="category-card">
            <div style="font-size: 2rem; margin-bottom: 0.5rem;">🛍️</div>
            <div class="category-name"><c:out value="${cat.categoryName}"/></div>
        </a>
    </c:forEach>
</div>

<!-- Featured Products -->
<div class="section-header">
    <h2 class="section-title">Featured Products</h2>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">Explore More &rarr;</a>
</div>

<div class="product-grid">
    <c:forEach var="prod" items="${featuredProducts}">
        <div class="product-card">
            <div class="product-card-img">
                <a href="${pageContext.request.contextPath}/product?id=${prod.productId}">
                    <img src="${pageContext.request.contextPath}/uploads/${prod.image}" alt="<c:out value='${prod.productName}'/>"
                         onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                </a>
            </div>
            <div class="product-card-body">
                <span class="product-card-category"><c:out value="${prod.categoryName}"/></span>
                <h3 class="product-card-title">
                    <a href="${pageContext.request.contextPath}/product?id=${prod.productId}">
                        <c:out value="${prod.productName}"/>
                    </a>
                </h3>
                <div class="product-card-price">
                    ₹<fmt:formatNumber value="${prod.price}" pattern="#,##0.00"/>
                </div>
                <div class="product-card-actions">
                    <a href="${pageContext.request.contextPath}/product?id=${prod.productId}" class="btn btn-outline btn-sm" style="flex: 1;">Details</a>
                    <button type="button" class="btn btn-primary btn-sm" style="flex: 1.2;"
                            onclick="ajaxAddToCart(${prod.productId}, 1, '${pageContext.request.contextPath}')">
                        Add to Cart
                    </button>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<!-- Value Props -->
<div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 2rem; margin: 3rem 0; display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; text-align: center;">
    <div>
        <div style="font-size: 2rem; margin-bottom: 0.5rem;">🚚</div>
        <h4 style="font-size: 1.1rem; margin-bottom: 0.3rem;">Fast Shipping</h4>
        <p style="color: var(--text-muted); font-size: 0.85rem;">Courier tracked doorstep delivery</p>
    </div>
    <div>
        <div style="font-size: 2rem; margin-bottom: 0.5rem;">🔒</div>
        <h4 style="font-size: 1.1rem; margin-bottom: 0.3rem;">Secure Transactions</h4>
        <p style="color: var(--text-muted); font-size: 0.85rem;">Cash on Delivery & online ready</p>
    </div>
    <div>
        <div style="font-size: 2rem; margin-bottom: 0.5rem;">⭐</div>
        <h4 style="font-size: 1.1rem; margin-bottom: 0.3rem;">Verified Reviews</h4>
        <p style="color: var(--text-muted); font-size: 0.85rem;">Reviews strictly by genuine buyers</p>
    </div>
    <div>
        <div style="font-size: 2rem; margin-bottom: 0.5rem;">💬</div>
        <h4 style="font-size: 1.1rem; margin-bottom: 0.3rem;">24/7 Feedback</h4>
        <p style="color: var(--text-muted); font-size: 0.85rem;">Direct feedback response system</p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
