<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Online Shopping System'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Top Info Bar -->
    <div class="top-bar">
        <div class="container">
            <span>IGNOU BCSP-064 BCA Final Year Project &bull; Online Shopping System</span>
            <div>
                <a href="${pageContext.request.contextPath}/contact" style="color: #cbd5e1; margin-right: 15px;">Support</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.admin}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" style="color: #60a5fa; font-weight: 600;">Admin Dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/admin/login" style="color: #94a3b8;">Admin Portal</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Main Navigation Header -->
    <header class="site-header">
        <div class="container nav-container">
            <!-- Brand Logo -->
            <a href="${pageContext.request.contextPath}/home" class="brand-logo">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                </svg>
                Shop<span>Hub</span>
            </a>

            <!-- Search Bar with Category Filter -->
            <form action="${pageContext.request.contextPath}/products" method="get" class="nav-search">
                <input type="text" name="keyword" placeholder="Search headphones, books, clothing..." value="<c:out value='${param.keyword}'/>">
                <button type="submit">Search</button>
            </form>

            <!-- Navigation Links & Account Menu -->
            <div class="nav-actions">
                <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/products" class="nav-link">Catalog</a>

                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/my-orders" class="nav-link">My Orders</a>
                        <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                            <strong><c:out value="${sessionScope.user.name}"/></strong>
                        </a>
                        <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: var(--danger);">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="nav-link">Login</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a>
                    </c:otherwise>
                </c:choose>

                <!-- Cart Button -->
                <a href="${pageContext.request.contextPath}/cart" class="cart-btn">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                    </svg>
                    Cart
                    <span class="cart-badge" id="nav-cart-badge">
                        <c:out value="${not empty sessionScope.cartCount ? sessionScope.cartCount : '0'}"/>
                    </span>
                </a>
            </div>
        </div>
    </header>

    <!-- Main Page Wrapper -->
    <main style="flex: 1; padding: 2rem 0;">
        <div class="container">
            <!-- Global Flash Messages -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success"><c:out value="${param.success}"/></div>
            </c:if>
            <c:if test="${not empty param.message}">
                <div class="alert alert-success"><c:out value="${param.message}"/></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger"><c:out value="${param.error}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger"><c:out value="${error}"/></div>
            </c:if>
