<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Contact Us & Feedback | ShopHub" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 3rem; margin: 2rem 0;">

    <!-- Contact Form -->
    <div class="cart-table-card">
        <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.6rem;">Send Us a Message</h2>
        <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 1.8rem;">
            Have questions about an order or recommendations for our catalog? We'd love to hear from you.
        </p>

        <form action="${pageContext.request.contextPath}/contact" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Your Name *</label>
                <input type="text" name="name" class="form-control" required placeholder="John Doe"
                       value="<c:out value='${not empty sessionScope.user ? sessionScope.user.name : name}'/>">
            </div>

            <div class="form-group">
                <label class="form-label">Your Email *</label>
                <input type="email" name="email" class="form-control" required placeholder="john.doe@example.com"
                       value="<c:out value='${not empty sessionScope.user ? sessionScope.user.email : email}'/>">
            </div>

            <div class="form-group">
                <label class="form-label">Message / Inquiry *</label>
                <textarea name="message" class="form-control" rows="5" required placeholder="Type your inquiry or feedback message here..."><c:out value="${message}"/></textarea>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">Submit Feedback</button>
        </form>
    </div>

    <!-- Contact Information Box -->
    <div style="display: flex; flex-direction: column; justify-content: center; gap: 1.5rem;">
        <div style="background: white; border: 1px solid var(--border); border-radius: var(--radius); padding: 2rem; box-shadow: var(--shadow-sm);">
            <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1rem;">Project Information</h3>
            <p style="color: var(--secondary); font-size: 0.95rem; line-height: 1.7; margin-bottom: 1.5rem;">
                <strong>Project:</strong> Online Shopping System<br>
                <strong>Course Code:</strong> IGNOU BCSP-064 (BCA Major Project)<br>
                <strong>Architecture:</strong> Model-View-Controller (MVC) + DAO Pattern<br>
                <strong>Server:</strong> Apache Tomcat 10 on Java 17 LTS
            </p>

            <h4 style="font-size: 1.05rem; font-weight: 700; margin-bottom: 0.5rem;">Operating Hours</h4>
            <p style="color: var(--text-muted); font-size: 0.9rem;">
                Monday &ndash; Saturday: 9:00 AM &ndash; 7:00 PM IST<br>
                Support Response Window: Within 24 Hours
            </p>
        </div>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
