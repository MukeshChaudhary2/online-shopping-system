package com.shopping.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for hashing and verifying passwords securely using BCrypt.
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Hashes a plain-text password using the BCrypt algorithm with a salt workload of 12.
     *
     * @param plainTextPassword The raw password entered by the user
     * @return The securely hashed password string
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Checks if a plain-text password matches an existing BCrypt hash.
     *
     * @param plainTextPassword The raw password entered by the user
     * @param hashedPassword   The stored BCrypt hash from the database
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}
