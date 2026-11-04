package com.shopping;

import com.shopping.model.CartItem;
import com.shopping.model.OrderItem;
import com.shopping.util.PasswordUtil;
import com.shopping.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationAndDAOTest {

    @Test
    public void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("john.doe@example.com"));
        assertTrue(ValidationUtil.isValidEmail("student_123@ignou.ac.in"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testPasswordValidation() {
        assertTrue(ValidationUtil.isValidPassword("Admin@123"));
        assertTrue(ValidationUtil.isValidPassword("12345678"));
        assertFalse(ValidationUtil.isValidPassword("short"));
        assertFalse(ValidationUtil.isValidPassword(null));
    }

    @Test
    public void testPhoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("+91-9876543210"));
        assertFalse(ValidationUtil.isValidPhone("abc"));
        assertFalse(ValidationUtil.isValidPhone(""));
    }

    @Test
    public void testImageFileValidation() {
        assertTrue(ValidationUtil.isValidImageFile("laptop.png", 1024 * 500));
        assertTrue(ValidationUtil.isValidImageFile("camera.jpg", 1024 * 1024));
        assertTrue(ValidationUtil.isValidImageFile("tshirt.webp", 1024 * 200));
        assertFalse(ValidationUtil.isValidImageFile("malicious.exe", 1024));
        assertFalse(ValidationUtil.isValidImageFile("script.sh", 1024));
        assertFalse(ValidationUtil.isValidImageFile("huge_file.png", 3 * 1024 * 1024)); // > 2MB
    }

    @Test
    public void testModelSubtotals() {
        CartItem cartItem = new CartItem();
        cartItem.setProductPrice(new BigDecimal("999.00"));
        cartItem.setQuantity(3);
        assertEquals(new BigDecimal("2997.00"), cartItem.getSubtotal());

        OrderItem orderItem = new OrderItem();
        orderItem.setUnitPrice(new BigDecimal("1499.50"));
        orderItem.setQuantity(2);
        assertEquals(new BigDecimal("2999.00"), orderItem.getSubtotal());
    }

    @Test
    public void testPasswordHashingIntegrity() {
        String raw = "StrongSecretPassword#2026";
        String hashed = PasswordUtil.hashPassword(raw);
        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$12$"));
        assertTrue(PasswordUtil.checkPassword(raw, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hashed));
    }
}
