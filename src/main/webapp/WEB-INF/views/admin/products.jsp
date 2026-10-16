<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Manage Products | ShopHub Admin" />
<c:set var="activePage" value="products" />
<jsp:include page="/WEB-INF/views/common/admin-header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 1.8rem; font-weight: 800; color: var(--text-main);">Product Inventory Management</h1>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Add, update, adjust stock levels, or deactivate catalog items.</p>
    </div>
    <button type="button" class="btn btn-primary" onclick="openModal('addProductModal')">+ Add New Product</button>
</div>

<div class="admin-card">
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Image</th>
                    <th>Product Name</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty products}">
                        <tr>
                            <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 2rem;">No products found in catalog.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td>
                                    <img src="${pageContext.request.contextPath}/uploads/${p.image}" alt="" class="thumb-small"
                                         onerror="this.src='${pageContext.request.contextPath}/uploads/default-product.png'">
                                </td>
                                <td>
                                    <strong><c:out value="${p.productName}"/></strong>
                                </td>
                                <td><c:out value="${p.categoryName}"/></td>
                                <td style="font-weight: 700;">₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                                <td>
                                    <span class="badge ${p.stock <= 5 ? 'badge-danger' : 'badge-success'}">
                                        ${p.stock} units
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${p.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                        <c:out value="${p.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 0.4rem;">
                                        <button type="button" class="btn btn-outline btn-sm"
                                                onclick="openEditProductModal('${p.productId}', '<c:out value='${p.categoryId}'/>', '<c:out value='${p.productName}'/>', '${p.price}', '${p.stock}', '<c:out value='${p.status}'/>', '<c:out value='${p.image}'/>', '<c:out value='${p.description}'/>')">
                                            Edit
                                        </button>

                                        <form action="${pageContext.request.contextPath}/admin/products/delete" method="post"
                                              onsubmit="return confirm('Are you sure you want to delete/deactivate this product?');" style="display: inline;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="productId" value="${p.productId}">
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

<!-- Add Product Modal -->
<div id="addProductModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Add New Catalog Product</h3>
            <button type="button" class="modal-close" onclick="closeModal('addProductModal')">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/admin/products/add" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label">Category *</label>
                <select name="categoryId" class="form-control" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}"><c:out value="${cat.categoryName}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label class="form-label">Product Name *</label>
                <input type="text" name="productName" class="form-control" required placeholder="e.g. Sony WH-1000XM5 Headphones">
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label">Price (₹) *</label>
                    <input type="number" step="0.01" min="0" name="price" class="form-control" required placeholder="2499.00">
                </div>
                <div class="form-group">
                    <label class="form-label">Stock Quantity *</label>
                    <input type="number" min="0" name="stock" class="form-control" required placeholder="25">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Product Image (JPG/PNG/WEBP, Max 2MB)</label>
                <input type="file" name="imageFile" class="form-control" accept="image/jpeg,image/png,image/webp" onchange="validateImageInput(this)">
            </div>

            <div class="form-group">
                <label class="form-label">Description</label>
                <textarea name="description" class="form-control" rows="3" placeholder="Key specs and feature highlights..."></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Status</label>
                <select name="status" class="form-control">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>

            <div style="display: flex; gap: 0.8rem; margin-top: 1.5rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Save Product</button>
                <button type="button" class="btn btn-outline" onclick="closeModal('addProductModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Product Modal -->
<div id="editProductModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Edit Product Details</h3>
            <button type="button" class="modal-close" onclick="closeModal('editProductModal')">&times;</button>
        </div>

        <form action="${pageContext.request.contextPath}/admin/products/edit" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" id="edit-productId" name="productId">
            <input type="hidden" id="edit-existingImage" name="existingImage">

            <div class="form-group">
                <label class="form-label">Category *</label>
                <select id="edit-categoryId" name="categoryId" class="form-control" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}"><c:out value="${cat.categoryName}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label class="form-label">Product Name *</label>
                <input type="text" id="edit-productName" name="productName" class="form-control" required>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label">Price (₹) *</label>
                    <input type="number" step="0.01" min="0" id="edit-price" name="price" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Stock Quantity *</label>
                    <input type="number" min="0" id="edit-stock" name="stock" class="form-control" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Replace Image (Optional, Max 2MB)</label>
                <input type="file" name="imageFile" class="form-control" accept="image/jpeg,image/png,image/webp" onchange="validateImageInput(this)">
            </div>

            <div class="form-group">
                <label class="form-label">Description</label>
                <textarea id="edit-description" name="description" class="form-control" rows="3"></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Status</label>
                <select id="edit-status" name="status" class="form-control">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>

            <div style="display: flex; gap: 0.8rem; margin-top: 1.5rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Update Product</button>
                <button type="button" class="btn btn-outline" onclick="closeModal('editProductModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openEditProductModal(id, catId, name, price, stock, status, image, desc) {
        document.getElementById('edit-productId').value = id;
        document.getElementById('edit-categoryId').value = catId;
        document.getElementById('edit-productName').value = name;
        document.getElementById('edit-price').value = price;
        document.getElementById('edit-stock').value = stock;
        document.getElementById('edit-status').value = status;
        document.getElementById('edit-existingImage').value = image;
        document.getElementById('edit-description').value = desc || '';
        openModal('editProductModal');
    }
</script>

<jsp:include page="/WEB-INF/views/common/admin-footer.jsp" />
