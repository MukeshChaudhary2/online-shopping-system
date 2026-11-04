<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Create Account | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card" style="max-width: 580px;">
    <h2 style="font-size: 1.6rem; font-weight: 800; text-align: center; margin-bottom: 0.5rem;">Create Your Account</h2>
    <p style="text-align: center; color: var(--text-muted); font-size: 0.95rem; margin-bottom: 2rem;">
        Join ShopHub today for seamless ordering and real-time tracking.
    </p>

    <form action="${pageContext.request.contextPath}/register" method="post" onsubmit="return validateRegisterForm(this)">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <div class="form-group">
            <label class="form-label">Full Name *</label>
            <input type="text" name="name" class="form-control" required placeholder="e.g. John Doe" value="<c:out value='${name}'/>">
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label class="form-label">Email Address *</label>
                <input type="email" name="email" class="form-control" required placeholder="john.doe@example.com" value="<c:out value='${email}'/>">
            </div>

            <div class="form-group">
                <label class="form-label">Phone Number *</label>
                <input type="tel" name="phone" class="form-control" required placeholder="9876543210" value="<c:out value='${phone}'/>">
            </div>
        </div>

        <div class="form-group">
            <label class="form-label">Shipping / Delivery Address *</label>
            <textarea name="address" class="form-control" rows="3" required placeholder="Flat/House, Street, City, State, PIN Code"><c:out value='${address}'/></textarea>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label class="form-label">Password * (Min 8 chars)</label>
                <input type="password" id="reg-password" name="password" class="form-control" required minlength="8" placeholder="••••••••">
            </div>

            <div class="form-group">
                <label class="form-label">Confirm Password *</label>
                <input type="password" id="reg-confirm" name="confirmPassword" class="form-control" required minlength="8" placeholder="••••••••">
            </div>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
            Register Account
        </button>
    </form>

    <div style="text-align: center; margin-top: 1.8rem; font-size: 0.9rem; color: var(--text-muted);">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login" style="font-weight: 600;">Sign in here</a>
    </div>
</div>

<script>
    function validateRegisterForm(form) {
        const pass = form.password.value;
        const confirm = form.confirmPassword.value;
        if (pass.length < 8) {
            alert("Password must be at least 8 characters long.");
            return false;
        }
        if (pass !== confirm) {
            alert("Passwords do not match. Please re-enter.");
            return false;
        }
        return true;
    }
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
