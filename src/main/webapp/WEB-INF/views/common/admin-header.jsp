<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Admin Panel | ShopHub'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">

    <!-- 1. Full-Width Top Bar Across The Absolute Top Of The Screen -->
    <header class="admin-topbar">
        <div>
            <strong style="color: var(--primary);">Administrator Portal</strong> &bull; IGNOU BCSP-064
        </div>
        <div style="display: flex; align-items: center; gap: 1.2rem;">
            <span style="font-size: 0.9rem; color: var(--secondary);">
                Logged in as <strong><c:out value="${sessionScope.admin.username}"/></strong>
            </span>
            <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #fca5a5;">
                Logout
            </a>
        </div>
    </header>

    <!-- 2. Admin Wrapper (Sidebar on Left + Content on Right "samne") -->
    <div class="admin-wrapper">
        <!-- Admin Sidebar -->
        <aside class="admin-sidebar">
            <div class="admin-brand">
                Shop<span>Hub</span> <small style="font-size: 0.75rem; color: #94a3b8; display: block; font-weight: 400;">Admin Console</small>
            </div>

            <ul class="admin-nav">
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                        📊 Dashboard
                    </a>
                </li>
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/products" class="admin-nav-link ${activePage == 'products' ? 'active' : ''}">
                        📦 Products Inventory
                    </a>
                </li>
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/categories" class="admin-nav-link ${activePage == 'categories' ? 'active' : ''}">
                        🏷️ Categories
                    </a>
                </li>
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/orders" class="admin-nav-link ${activePage == 'orders' ? 'active' : ''}">
                        🚚 Orders & Tracking
                    </a>
                </li>
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/reports" class="admin-nav-link ${activePage == 'reports' ? 'active' : ''}">
                        📈 Reports & Exports
                    </a>
                </li>
                <li class="admin-nav-item">
                    <a href="${pageContext.request.contextPath}/admin/feedbacks" class="admin-nav-link ${activePage == 'feedbacks' ? 'active' : ''}">
                        💬 Customer Feedbacks
                    </a>
                </li>
            </ul>

            <div style="padding: 1.5rem; border-top: 1px solid #1e293b;">
                <a href="${pageContext.request.contextPath}/home" target="_blank" style="color: #94a3b8; font-size: 0.85rem; display: flex; align-items: center; gap: 0.5rem;">
                    ↗ View Live Store
                </a>
            </div>
        </aside>

        <!-- Admin Main Content Body (Positioned right in front of sidebar) -->
        <div class="admin-main">
            <div class="admin-content">
                <!-- Global Flash Messages -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success"><c:out value="${param.success}"/></div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger"><c:out value="${param.error}"/></div>
                </c:if>
