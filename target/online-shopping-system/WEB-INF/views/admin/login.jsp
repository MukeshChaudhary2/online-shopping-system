<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Portal Login | ShopHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body style="background: #0f172a; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 1.5rem;">

    <div style="background: white; border-radius: var(--radius); padding: 2.5rem; max-width: 440px; width: 100%; box-shadow: var(--shadow-lg);">
        <div style="text-align: center; margin-bottom: 2rem;">
            <div style="display: inline-flex; align-items: center; gap: 0.5rem; font-size: 1.5rem; font-weight: 800; color: #0f172a; margin-bottom: 0.5rem;">
                Shop<span style="color: var(--primary);">Hub</span>
            </div>
            <h2 style="font-size: 1.3rem; font-weight: 700; color: var(--text-main);">Administrator Sign In</h2>
            <p style="color: var(--text-muted); font-size: 0.85rem;">Authorized management personnel only</p>
        </div>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger"><c:out value="${param.error}"/></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger"><c:out value="${error}"/></div>
        </c:if>
        <c:if test="${not empty param.message}">
            <div class="alert alert-success"><c:out value="${param.message}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/login" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Admin Username *</label>
                <input type="text" name="username" class="form-control" required placeholder="admin" autofocus>
            </div>

            <div class="form-group">
                <label class="form-label">Password *</label>
                <input type="password" name="password" class="form-control" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
                Authenticate & Enter
            </button>
        </form>

        <div style="background: #f1f5f9; border-radius: var(--radius-sm); padding: 0.8rem; margin-top: 1.8rem; font-size: 0.8rem; color: var(--secondary); text-align: center;">
            <strong>Default Evaluator Credentials:</strong><br>
            Username: <code>admin</code><br>
            Password: <code>Admin@123</code>
        </div>

        <div style="text-align: center; margin-top: 1.5rem;">
            <a href="${pageContext.request.contextPath}/home" style="font-size: 0.85rem; color: var(--text-muted);">&larr; Return to Customer Storefront</a>
        </div>
    </div>

</body>
</html>
