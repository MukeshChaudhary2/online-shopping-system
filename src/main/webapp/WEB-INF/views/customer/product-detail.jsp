<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="${product.productName} | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Product Detail Layout -->
<div class="product-detail-layout">

    <!-- Gallery Image -->
    <div class="detail-gallery">
        <img src="${pageContext.request.contextPath}/uploads/${product.image}" alt="<c:out value='${product.productName}'/>"
             onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
    </div>

    <!-- Product Details -->
    <div class="detail-info">
        <span class="detail-category"><c:out value="${product.categoryName}"/></span>
        <h1 class="detail-title"><c:out value="${product.productName}"/></h1>

        <!-- Star Rating Summary -->
        <div class="detail-rating">
            <span style="color: #f59e0b; font-size: 1.2rem;">★</span>
            <strong>${avgRating > 0 ? avgRating : 'No reviews'}</strong>
            <span style="color: var(--text-muted);">(${reviewCount} verified ratings)</span>
        </div>

        <div class="detail-price">
            ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
        </div>

        <div class="detail-meta">
            <c:choose>
                <c:when test="${product.stock > 0}">
                    <span class="badge badge-success">In Stock (${product.stock} units available)</span>
                </c:when>
                <c:otherwise>
                    <span class="badge badge-danger">Out of Stock</span>
                </c:otherwise>
            </c:choose>
        </div>

        <p class="detail-desc"><c:out value="${product.description}"/></p>

        <!-- Add to Cart Form -->
        <c:if test="${product.stock > 0}">
            <form action="${pageContext.request.contextPath}/cart/add" method="post" class="detail-actions" style="margin-top: auto;">
                <input type="hidden" name="productId" value="${product.productId}">
                <label for="detail-qty" style="font-weight: 600;">Qty:</label>
                <input type="number" id="detail-qty" name="quantity" class="qty-input" value="1" min="1" max="${product.stock}">

                <button type="submit" class="btn btn-primary btn-lg">Add to Cart</button>
            </form>
        </c:if>
    </div>
</div>

<!-- Customer Reviews Section -->
<div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 2rem; margin-bottom: 2.5rem;">
    <h3 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 1.5rem; border-bottom: 1px solid var(--border); padding-bottom: 0.8rem;">
        Customer Reviews & Ratings
    </h3>

    <!-- Verified Buyer Review Form -->
    <c:choose>
        <c:when test="${canReview and not alreadyReviewed}">
            <div style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 1.5rem; margin-bottom: 2rem;">
                <h4 style="font-size: 1.1rem; margin-bottom: 0.8rem; color: var(--text-main);">Leave a Verified Review</h4>
                <form action="${pageContext.request.contextPath}/submit-review" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="productId" value="${product.productId}">

                    <div class="form-group">
                        <label class="form-label">Your Rating (1 to 5 Stars)</label>
                        <select name="rating" class="form-control" style="max-width: 200px;" required>
                            <option value="5">★★★★★ (5 - Excellent)</option>
                            <option value="4">★★★★☆ (4 - Very Good)</option>
                            <option value="3">★★★☆☆ (3 - Average)</option>
                            <option value="2">★★☆☆☆ (2 - Below Average)</option>
                            <option value="1">★☆☆☆☆ (1 - Poor)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Review Comments</label>
                        <textarea name="review" class="form-control" rows="3" placeholder="Share your experience with this item..." required></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Submit Review</button>
                </form>
            </div>
        </c:when>
        <c:when test="${alreadyReviewed}">
            <div class="alert alert-success" style="margin-bottom: 1.5rem;">
                You have already submitted a verified review for this product. Thank you!
            </div>
        </c:when>
        <c:otherwise>
            <div style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 1rem 1.2rem; margin-bottom: 1.5rem; font-size: 0.9rem; color: var(--text-muted);">
                ℹ️ <em>Only verified customers who have purchased this product can submit a review.</em>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Reviews List -->
    <c:choose>
        <c:when test="${empty reviews}">
            <p style="color: var(--text-muted); padding: 1rem 0;">No reviews yet for this product. Be the first purchaser to leave feedback!</p>
        </c:when>
        <c:otherwise>
            <div style="display: flex; flex-direction: column; gap: 1.2rem;">
                <c:forEach var="rev" items="${reviews}">
                    <div style="border-bottom: 1px solid var(--border); padding-bottom: 1rem;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.4rem;">
                            <strong><c:out value="${rev.userName}"/> <span class="badge badge-success" style="font-size: 0.7rem; margin-left: 6px;">Verified Buyer</span></strong>
                            <span style="color: var(--text-muted); font-size: 0.8rem;"><fmt:formatDate value="${rev.createdAt}" pattern="dd MMM yyyy"/></span>
                        </div>
                        <div style="color: #f59e0b; margin-bottom: 0.4rem;">
                            <c:forEach begin="1" end="${rev.rating}">★</c:forEach><c:forEach begin="${rev.rating + 1}" end="5">☆</c:forEach>
                        </div>
                        <p style="color: var(--secondary); font-size: 0.95rem;"><c:out value="${rev.review}"/></p>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
