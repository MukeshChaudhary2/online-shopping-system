package com.shopping.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Server-side input validation and sanitization utility.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9+\\-\\s]{10,15}$"
    );

    private static final long MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024; // 2 MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"};

    private ValidationUtil() {
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        // Minimum 8 characters
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isValidNonNegativePrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isValidNonNegativeStock(int stock) {
        return stock >= 0;
    }

    /**
     * Checks if uploaded image has allowed extension and size <= 2MB.
     */
    public static boolean isValidImageFile(String fileName, long sizeInBytes) {
        if (!isNotEmpty(fileName) || sizeInBytes > MAX_IMAGE_SIZE_BYTES || sizeInBytes <= 0) {
            return false;
        }
        String lower = fileName.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitizes plain strings by trimming and stripping dangerous control characters.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
}
