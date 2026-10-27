package com.shopping.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for Cross-Site Request Forgery (CSRF) protection using synchronized session tokens.
 */
public class CSRFUtil {

    public static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    public static final String CSRF_TOKEN_PARAM_NAME = "csrfToken";
    public static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";

    private static final SecureRandom secureRandom = new SecureRandom();

    private CSRFUtil() {
    }

    /**
     * Retrieves the existing CSRF token from session, or generates a new one if not present.
     *
     * @param session The active user HttpSession
     * @return 32-byte secure Base64 token string
     */
    public static String getToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (token == null) {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, token);
        }
        return token;
    }

    /**
     * Validates whether the incoming request contains a valid matching CSRF token.
     *
     * @param request The HttpServletRequest
     * @return true if valid, false otherwise
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        // Check header first (e.g., for AJAX/fetch calls), then fallback to form parameter
        String requestToken = request.getHeader(CSRF_TOKEN_HEADER_NAME);
        if (requestToken == null || requestToken.trim().isEmpty()) {
            requestToken = request.getParameter(CSRF_TOKEN_PARAM_NAME);
        }

        return sessionToken.equals(requestToken);
    }
}
