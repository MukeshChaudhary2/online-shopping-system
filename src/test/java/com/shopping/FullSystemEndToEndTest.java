package com.shopping;

import com.shopping.dao.*;
import com.shopping.model.*;
import com.shopping.util.PasswordUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullSystemEndToEndTest {

    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final TrackingDAO trackingDAO = new TrackingDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Test
    @Order(1)
    public void testAdminLogin() {
        System.out.println("--> Testing Admin Login...");
        Admin admin = adminDAO.getAdminByUsername("admin");
        assertNotNull(admin, "Default admin should exist in database");
        assertTrue(PasswordUtil.checkPassword("Admin@123", admin.getPassword()), "Admin password hash must match Admin@123");
        System.out.println("Admin login test PASSED for: " + admin.getUsername());
    }

    @Test
    @Order(2)
    public void testCustomerLoginWithSampleData() {
        System.out.println("--> Testing Customer Login with Sample Data...");
        User user = userDAO.getUserByEmail("john.doe@example.com");
        assertNotNull(user, "Default customer john.doe@example.com should exist");
        assertTrue(PasswordUtil.checkPassword("Customer@123", user.getPassword()), "Customer password hash must match Customer@123");
        System.out.println("Customer login test PASSED for: " + user.getEmail() + " (" + user.getName() + ")");
    }

    @Test
    @Order(3)
    public void testNewCustomerRegistrationAndSubsequentLogin() {
        System.out.println("--> Testing New Customer Registration...");
        long timestamp = System.currentTimeMillis();
        String email = "student_" + timestamp + "@ignou.ac.in";
        String password = "Password@2026";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User newUser = new User("IGNOU Final Year Student", email, hashedPassword, "9876501234", "Sector 62, Noida, UP");
        boolean registered = userDAO.registerUser(newUser);
        assertTrue(registered, "User registration should return true");
        assertTrue(newUser.getUserId() > 0, "User ID should be generated");

        System.out.println("Registered user ID: " + newUser.getUserId());

        // Test duplicate email check
        assertTrue(userDAO.isEmailRegistered(email), "isEmailRegistered must return true for new user");

        // Verify login with the newly created account
        User fetched = userDAO.getUserByEmail(email);
        assertNotNull(fetched, "Should be able to fetch newly registered user");
        assertEquals(newUser.getName(), fetched.getName());
        assertTrue(PasswordUtil.checkPassword(password, fetched.getPassword()), "Password verification for new user must succeed");
        System.out.println("New customer registration and login test PASSED!");
    }

    @Test
    @Order(4)
    public void testCatalogAndProducts() {
        System.out.println("--> Testing Catalog Categories and Products...");
        List<Category> categories = categoryDAO.getActiveCategories();
        assertFalse(categories.isEmpty(), "Categories list should not be empty");
        System.out.println("Active categories count: " + categories.size());

        List<Product> products = productDAO.getAllProducts();
        assertFalse(products.isEmpty(), "Products should not be empty");
        System.out.println("Products count: " + products.size());

        Product first = products.get(0);
        assertNotNull(first.getProductName());
        assertTrue(first.getPrice().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(first.getStock() > 0);
    }

    @Test
    @Order(5)
    public void testCartAndOrderCheckoutTransaction() throws Exception {
        System.out.println("--> Testing Cart & Order Checkout Transaction...");
        User user = userDAO.getUserByEmail("john.doe@example.com");
        assertNotNull(user);

        List<Product> products = productDAO.getAllProducts();
        Product product = products.get(0);

        int initialStock = product.getStock();
        int qtyToBuy = 2;

        // 1. Add to Cart
        boolean added = cartDAO.addToCart(user.getUserId(), product.getProductId(), qtyToBuy);
        assertTrue(added, "Should add product to cart");

        List<CartItem> cartItems = cartDAO.getCartByUserId(user.getUserId());
        assertFalse(cartItems.isEmpty(), "Cart should contain items");

        // 2. Place Order (ACID transaction)
        com.shopping.model.Order order = new com.shopping.model.Order();
        order.setUserId(user.getUserId());
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(qtyToBuy)));
        order.setShippingAddress("123 Test Street, New Delhi");
        order.setOrderStatus("PENDING");

        int orderId = orderDAO.placeOrderTransaction(order, cartItems, "COD", "Blue Dart Express");
        assertTrue(orderId > 0, "Order ID should be positive");
        System.out.println("Created Order ID: " + orderId);

        // 3. Verify Cart is cleared
        List<CartItem> cartAfterOrder = cartDAO.getCartByUserId(user.getUserId());
        assertTrue(cartAfterOrder.isEmpty(), "Cart should be empty after checkout");

        // 4. Verify Stock was decremented
        Product updatedProduct = productDAO.getProductById(product.getProductId());
        assertEquals(initialStock - qtyToBuy, updatedProduct.getStock(), "Stock should be decremented accurately");

        // 5. Verify Tracking record was generated
        TrackingDetail tracking = trackingDAO.getTrackingByOrderId(orderId);
        assertNotNull(tracking, "Tracking record should be generated for the order");
        assertEquals("ORDER_PLACED", tracking.getDeliveryStatus());

        System.out.println("Cart and Order transaction test PASSED!");
    }

    @Test
    @Order(6)
    public void testFeedbackSubmission() {
        System.out.println("--> Testing Feedback submission...");
        Feedback feedback = new Feedback("Student Tester", "tester@test.com", "Project runs cleanly and successfully!");
        boolean submitted = feedbackDAO.saveFeedback(feedback);
        assertTrue(submitted, "Feedback should be saved");
        List<Feedback> all = feedbackDAO.getAllFeedbacks();
        assertFalse(all.isEmpty(), "Feedback list should have entries");
        System.out.println("Feedback test PASSED!");
    }
}
