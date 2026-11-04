<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Product Catalog | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: grid; grid-template-columns: 260px 1fr; gap: 2rem; align-items: start;">

    <!-- Filter Sidebar -->
    <aside style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 1.5rem; box-shadow: var(--shadow-sm);">
        <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">Filter Products</h3>
        <form action="${pageContext.request.contextPath}/products" method="get">

            <!-- Keyword Search -->
            <div class="form-group">
                <label class="form-label">Keyword</label>
                <input type="text" name="keyword" class="form-control" placeholder="Search title..." value="<c:out value='${keyword}'/>">
            </div>

            <!-- Categories -->
            <div class="form-group">
                <label class="form-label">Category</label>
                <select name="category" class="form-control">
                    <option value="">All Categories</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}" ${selectedCategory == cat.categoryId ? 'selected' : ''}>
                            <c:out value="${cat.categoryName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Price Range -->
            <div class="form-group">
                <label class="form-label">Price Range (₹)</label>
                <div style="display: flex; gap: 0.5rem;">
                    <input type="number" name="minPrice" class="form-control" placeholder="Min" value="<c:out value='${minPrice}'/>" min="0">
                    <input type="number" name="maxPrice" class="form-control" placeholder="Max" value="<c:out value='${maxPrice}'/>" min="0">
                </div>
            </div>

            <!-- Sort By -->
            <div class="form-group">
                <label class="form-label">Sort By</label>
                <select name="sort" class="form-control">
                    <option value="">Newest Arrivals</option>
                    <option value="price_asc" ${'price_asc'.equalsIgnoreCase(sort) ? 'selected' : ''}>Price: Low to High</option>
                    <option value="price_desc" ${'price_desc'.equalsIgnoreCase(sort) ? 'selected' : ''}>Price: High to Low</option>
                    <option value="name_asc" ${'name_asc'.equalsIgnoreCase(sort) ? 'selected' : ''}>Name: A to Z</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="margin-bottom: 0.6rem;">Apply Filters</button>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-block btn-sm">Clear Filters</a>
        </form>
    </aside>

    <!-- Product Listing Main Content -->
    <div>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem;">
            <h2 style="font-size: 1.4rem; font-weight: 700;">
                <c:choose>
                    <c:when test="${not empty keyword}">
                        Search results for "<c:out value="${keyword}"/>"
                    </c:when>
                    <c:otherwise>
                        All Products (${products.size()} items)
                    </c:otherwise>
                </c:choose>
            </h2>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 3rem; text-align: center;">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">🔍</div>
                    <h3 style="font-size: 1.2rem; margin-bottom: 0.5rem;">No products found</h3>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Try adjusting your keyword or filters to find what you're looking for.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Reset Filters</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid" style="grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));">
                    <c:forEach var="prod" items="${products}">
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
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
