package com.shopping;

import com.shopping.dao.UserDAO;
import com.shopping.model.User;
import com.shopping.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestLiveUserRegistrationTest {

    @Test
    public void testLiveRegisterAndLogin() {
        UserDAO userDAO = new UserDAO();

        String testEmail = "test_student_" + System.currentTimeMillis() + "@example.com";
        String rawPassword = "Password@123";
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        User newUser = new User(
                "Mukesh Kumar",
                testEmail,
                hashedPassword,
                "9876543210",
                "Sector 12, Dwarka, New Delhi"
        );

        System.out.println("Attempting to register user: " + testEmail);
        boolean registered = userDAO.registerUser(newUser);
        System.out.println("Register result: " + registered + ", Generated user_id: " + newUser.getUserId());
        assertTrue(registered, "User registration should succeed in MySQL");
        assertTrue(newUser.getUserId() > 0, "Generated user_id should be > 0");

        // Now test fetch by email (Login check)
        User fetched = userDAO.getUserByEmail(testEmail);
        assertNotNull(fetched, "Fetched user should not be null");
        System.out.println("Fetched user from DB: " + fetched.getName() + " with ID: " + fetched.getUserId());

        boolean passMatches = PasswordUtil.checkPassword(rawPassword, fetched.getPassword());
        System.out.println("Password match verification: " + passMatches);
        assertTrue(passMatches, "Password should verify with BCrypt");
    }
}
