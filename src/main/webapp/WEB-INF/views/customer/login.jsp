<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Customer Login | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card">
    <h2 style="font-size: 1.6rem; font-weight: 800; text-align: center; margin-bottom: 0.5rem;">Welcome Back</h2>
    <p style="text-align: center; color: var(--text-muted); font-size: 0.95rem; margin-bottom: 2rem;">
        Sign in to manage your orders, wishlist, and profile.
    </p>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <div class="form-group">
            <label class="form-label">Email Address *</label>
            <input type="email" name="email" class="form-control" required placeholder="john.doe@example.com" value="<c:out value='${email}'/>">
        </div>

        <div class="form-group">
            <label class="form-label">Password *</label>
            <input type="password" name="password" class="form-control" required placeholder="••••••••">
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
            Sign In to Account
        </button>
    </form>

    <div style="text-align: center; margin-top: 1.8rem; font-size: 0.9rem; color: var(--text-muted);">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Create one now</a>
    </div>

    <!-- Quick Credentials Hint for Project Evaluation -->
    <div style="background: #f1f5f9; border-radius: var(--radius-sm); padding: 0.8rem; margin-top: 1.8rem; font-size: 0.8rem; color: var(--secondary);">
        <strong>Demo Credentials:</strong><br>
        Email: <code>john.doe@example.com</code><br>
        Password: <code>Customer@123</code>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
