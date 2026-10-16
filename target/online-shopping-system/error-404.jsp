<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found | Online Shopping System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="error-page-body">
    <div class="error-container">
        <div class="error-card">
            <div class="error-code">404</div>
            <h1 class="error-title">Page Not Found</h1>
            <p class="error-desc">The page or resource you are looking for does not exist, has been removed, or is temporarily unavailable.</p>
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Return to Homepage</a>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">Browse Catalog</a>
            </div>
        </div>
    </div>
</body>
</html>
