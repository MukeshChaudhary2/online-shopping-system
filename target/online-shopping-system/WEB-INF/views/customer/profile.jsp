<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="My Profile | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 1.8rem;">Customer Profile & Settings</h1>

<div style="display: grid; grid-template-columns: 1.2fr 1fr; gap: 2rem;">

    <!-- Personal Info & Shipping Address Form -->
    <div class="cart-table-card">
        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
            Account Details
        </h3>

        <c:if test="${not empty profileError}">
            <div class="alert alert-danger"><c:out value="${profileError}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/profile" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Email Address (Read Only)</label>
                <input type="email" class="form-control" value="<c:out value='${sessionScope.user.email}'/>" disabled style="background: #f1f5f9;">
            </div>

            <div class="form-group">
                <label class="form-label">Full Name *</label>
                <input type="text" name="name" class="form-control" required value="<c:out value='${sessionScope.user.name}'/>">
            </div>

            <div class="form-group">
                <label class="form-label">Contact Phone *</label>
                <input type="tel" name="phone" class="form-control" required value="<c:out value='${sessionScope.user.phone}'/>">
            </div>

            <div class="form-group">
                <label class="form-label">Default Shipping Address *</label>
                <textarea name="address" class="form-control" rows="4" required><c:out value='${sessionScope.user.address}'/></textarea>
            </div>

            <button type="submit" class="btn btn-primary">Save Profile Changes</button>
        </form>
    </div>

    <!-- Change Password Form -->
    <div class="cart-table-card">
        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.6rem;">
            Security & Password
        </h3>

        <c:if test="${not empty passwordError}">
            <div class="alert alert-danger"><c:out value="${passwordError}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/profile/change-password" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Current Password *</label>
                <input type="password" name="currentPassword" class="form-control" required placeholder="••••••••">
            </div>

            <div class="form-group">
                <label class="form-label">New Password * (Min 8 chars)</label>
                <input type="password" name="newPassword" class="form-control" required minlength="8" placeholder="••••••••">
            </div>

            <div class="form-group">
                <label class="form-label">Confirm New Password *</label>
                <input type="password" name="confirmNewPassword" class="form-control" required minlength="8" placeholder="••••••••">
            </div>

            <button type="submit" class="btn btn-outline" style="border-color: var(--primary); color: var(--primary);">
                Update Password
            </button>
        </form>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
