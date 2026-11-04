package com.shopping.servlet;

import com.shopping.dao.CategoryDAO;
import com.shopping.dao.ProductDAO;
import com.shopping.model.Category;
import com.shopping.model.Product;
import com.shopping.util.CSRFUtil;
import com.shopping.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for Admin Product Inventory management.
 * Handles product creation, updating, safe deletion, and secure image uploads.
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = {
        "/admin/products",
        "/admin/products/add",
        "/admin/products/edit",
        "/admin/products/delete"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB buffer
        maxFileSize = 2 * 1024 * 1024,         // 2 MB max file size
        maxRequestSize = 5 * 1024 * 1024       // 5 MB max request size
)
public class AdminProductServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminProductServlet.class.getName());
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        CSRFUtil.getToken(request.getSession(true));

        request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!CSRFUtil.isValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }

        String path = request.getServletPath();

        if ("/admin/products/add".equals(path)) {
            handleAdd(request, response);
        } else if ("/admin/products/edit".equals(path)) {
            handleEdit(request, response);
        } else if ("/admin/products/delete".equals(path)) {
            handleDelete(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String productName = ValidationUtil.sanitize(request.getParameter("productName"));
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            String description = ValidationUtil.sanitize(request.getParameter("description"));
            String status = ValidationUtil.sanitize(request.getParameter("status"));

            if (!ValidationUtil.isNotEmpty(productName) || !ValidationUtil.isValidNonNegativePrice(price) || !ValidationUtil.isValidNonNegativeStock(stock)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=Invalid input values.");
                return;
            }

            String uploadedFileName = processUploadedImage(request.getPart("imageFile"), request);
            if (uploadedFileName == null) {
                uploadedFileName = "default-product.png";
            }

            Product product = new Product(categoryId, productName, price, stock, uploadedFileName, description,
                    "INACTIVE".equalsIgnoreCase(status) ? "INACTIVE" : "ACTIVE");

            boolean success = productDAO.addProduct(product);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=Product added successfully.");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=Failed to add product.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error adding product", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=Invalid form submission.");
        }
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String productName = ValidationUtil.sanitize(request.getParameter("productName"));
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            String description = ValidationUtil.sanitize(request.getParameter("description"));
            String status = ValidationUtil.sanitize(request.getParameter("status"));
            String existingImage = request.getParameter("existingImage");

            if (!ValidationUtil.isNotEmpty(productName) || !ValidationUtil.isValidNonNegativePrice(price) || !ValidationUtil.isValidNonNegativeStock(stock)) {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=Invalid input values.");
                return;
            }

            String uploadedFileName = processUploadedImage(request.getPart("imageFile"), request);
            String finalImage = (uploadedFileName != null) ? uploadedFileName : existingImage;
            if (finalImage == null || finalImage.trim().isEmpty()) {
                finalImage = "default-product.png";
            }

            Product product = new Product();
            product.setProductId(productId);
            product.setCategoryId(categoryId);
            product.setProductName(productName);
            product.setPrice(price);
            product.setStock(stock);
            product.setImage(finalImage);
            product.setDescription(description);
            product.setStatus("INACTIVE".equalsIgnoreCase(status) ? "INACTIVE" : "ACTIVE");

            boolean success = productDAO.updateProduct(product);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=Product updated successfully.");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=Failed to update product.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error editing product", e);
            response.sendRedirect(request.getContextPath() + "/admin/products?error=Invalid form submission.");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            boolean success = productDAO.deleteProduct(productId);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?success=Product deleted or deactivated.");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?error=Failed to delete product.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/products?error=Invalid product ID.");
        }
    }

    /**
     * Validates and saves uploaded image file.
     * Enforces file extension limits (jpg, png, webp) and random UUID file renaming.
     */
    private String processUploadedImage(Part filePart, HttpServletRequest request) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (!ValidationUtil.isValidImageFile(submittedFileName, filePart.getSize())) {
            return null;
        }

        String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.')).toLowerCase();
        String uniqueFileName = "prod_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Save to uploads directory inside webapp
        String uploadPath = request.getServletContext().getRealPath("/uploads");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        filePart.write(uploadPath + File.separator + uniqueFileName);
        return uniqueFileName;
    }
}
