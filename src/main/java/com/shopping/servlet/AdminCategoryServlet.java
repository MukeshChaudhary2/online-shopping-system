package com.shopping.servlet;

import com.shopping.dao.CategoryDAO;
import com.shopping.model.Category;
import com.shopping.util.CSRFUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Admin Category CRUD operations.
 */
@WebServlet(name = "AdminCategoryServlet", urlPatterns = {
        "/admin/categories",
        "/admin/categories/add",
        "/admin/categories/edit",
        "/admin/categories/delete"
})
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        CSRFUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/WEB-INF/views/admin/categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String path = request.getServletPath();

        if ("/admin/categories/add".equals(path)) {
            handleAdd(request, response);
        } else if ("/admin/categories/edit".equals(path)) {
            handleEdit(request, response);
        } else if ("/admin/categories/delete".equals(path)) {
            handleDelete(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String name = ValidationUtil.sanitize(request.getParameter("categoryName"));
        String status = ValidationUtil.sanitize(request.getParameter("status"));

        if (!ValidationUtil.isNotEmpty(name)) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=Category name cannot be empty.");
            return;
        }

        if (categoryDAO.isCategoryNameExists(name, 0)) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=Category with this name already exists.");
            return;
        }

        Category cat = new Category(name, "INACTIVE".equalsIgnoreCase(status) ? "INACTIVE" : "ACTIVE");
        categoryDAO.addCategory(cat);
        response.sendRedirect(request.getContextPath() + "/admin/categories?success=Category added successfully.");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String name = ValidationUtil.sanitize(request.getParameter("categoryName"));
            String status = ValidationUtil.sanitize(request.getParameter("status"));

            if (!ValidationUtil.isNotEmpty(name)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=Category name cannot be empty.");
                return;
            }

            if (categoryDAO.isCategoryNameExists(name, categoryId)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=Another category already uses this name.");
                return;
            }

            Category cat = new Category();
            cat.setCategoryId(categoryId);
            cat.setCategoryName(name);
            cat.setStatus("INACTIVE".equalsIgnoreCase(status) ? "INACTIVE" : "ACTIVE");

            categoryDAO.updateCategory(cat);
            response.sendRedirect(request.getContextPath() + "/admin/categories?success=Category updated successfully.");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=Invalid category ID.");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));

            if (categoryDAO.hasProducts(categoryId)) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=Cannot delete category: products are assigned to it.");
                return;
            }

            categoryDAO.deleteCategory(categoryId);
            response.sendRedirect(request.getContextPath() + "/admin/categories?success=Category deleted successfully.");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=Invalid category ID.");
        }
    }
}
