<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Internal Server Error | Online Shopping System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="error-page-body">
    <div class="error-container">
        <div class="error-card">
            <div class="error-code text-danger">500</div>
            <h1 class="error-title">Internal Server Error</h1>
            <p class="error-desc">An unexpected error occurred while processing your request. Our system engineers have been alerted.</p>
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Return to Homepage</a>
                <a href="${pageContext.request.contextPath}/contact" class="btn btn-outline">Report Issue</a>
            </div>
        </div>
    </div>
</body>
</html>
