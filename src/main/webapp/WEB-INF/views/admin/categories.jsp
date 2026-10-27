<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Manage Categories | ShopHub Admin" />
<c:set var="activePage" value="categories" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Product Categories</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Organize catalog items into navigable departments.</p>
    </div>
    <button type="button" class="btn btn-primary" onclick="openModal('addCategoryModal')">+ Add New Category</button>
</div>

<div class="admin-card">
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Category ID</th>
                    <th>Category Name</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 2rem;">No categories defined yet.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="c" items="${categories}">
                            <tr>
                                <td>#${c.categoryId}</td>
                                <td><strong><c:out value="${c.categoryName}"/></strong></td>
                                <td>
                                    <span class="badge ${c.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                        <c:out value="${c.status}"/>
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${c.createdAt}" pattern="dd MMM yyyy"/></td>
                                <td>
                                    <div style="display: flex; gap: 0.4rem;">
                                        <button type="button" class="btn btn-outline btn-sm"
                                                onclick="openEditCategoryModal('${c.categoryId}', '<c:out value='${c.categoryName}'/>', '<c:out value='${c.status}'/>')">
                                            Edit
                                        </button>

                                        <form action="${pageContext.request.contextPath}/admin/categories/delete" method="post"
                                              onsubmit="return confirm('Delete this category? Cannot delete if products are linked.');" style="display: inline;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="categoryId" value="${c.categoryId}">
                                            <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #fca5a5;">
                                                Delete
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Add Category Modal -->
<div id="addCategoryModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Add New Category</h3>
            <button type="button" class="modal-close" onclick="closeModal('addCategoryModal')">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/admin/categories/add" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Category Name *</label>
                <input type="text" name="categoryName" class="form-control" required placeholder="e.g. Gaming & Consoles">
            </div>

            <div class="form-group">
                <label class="form-label">Status</label>
                <select name="status" class="form-control">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>

            <div style="display: flex; gap: 0.8rem; margin-top: 1.5rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Save Category</button>
                <button type="button" class="btn btn-outline" onclick="closeModal('addCategoryModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Category Modal -->
<div id="editCategoryModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Edit Category</h3>
            <button type="button" class="modal-close" onclick="closeModal('editCategoryModal')">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/admin/categories/edit" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" id="edit-catId" name="categoryId">

            <div class="form-group">
                <label class="form-label">Category Name *</label>
                <input type="text" id="edit-catName" name="categoryName" class="form-control" required>
            </div>

            <div class="form-group">
                <label class="form-label">Status</label>
                <select id="edit-catStatus" name="status" class="form-control">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>

            <div style="display: flex; gap: 0.8rem; margin-top: 1.5rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Update Category</button>
                <button type="button" class="btn btn-outline" onclick="closeModal('editCategoryModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openEditCategoryModal(id, name, status) {
        document.getElementById('edit-catId').value = id;
        document.getElementById('edit-catName').value = name;
        document.getElementById('edit-catStatus').value = status;
        openModal('editCategoryModal');
    }
</script>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
