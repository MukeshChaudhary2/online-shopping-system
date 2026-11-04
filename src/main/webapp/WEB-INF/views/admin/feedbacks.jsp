<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Customer Inquiries & Feedback | ShopHub Admin" />
<c:set var="activePage" value="feedbacks" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Customer Inquiries & Feedback</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Visitor questions submitted via the public contact portal.</p>
    </div>
</div>

<div class="admin-card">
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Customer Name</th>
                    <th>Email</th>
                    <th>Message</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty feedbacks}">
                        <tr>
                            <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No feedback messages received yet.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="f" items="${feedbacks}">
                            <tr>
                                <td style="white-space: nowrap;"><fmt:formatDate value="${f.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td><strong><c:out value="${f.name}"/></strong></td>
                                <td><a href="mailto:${f.email}"><c:out value="${f.email}"/></a></td>
                                <td style="max-width: 450px;"><c:out value="${f.message}"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/feedbacks/delete" method="post"
                                          onsubmit="return confirm('Delete this feedback message?');">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="${f.id}">
                                        <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #fca5a5;">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
