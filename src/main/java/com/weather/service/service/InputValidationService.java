package com.weather.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class InputValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern SAFE_STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.]+$");
    
    // SQL injection patterns
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("(?i).*(union|select|insert|update|delete|drop|create|alter|exec|execute).*"),
        Pattern.compile("(?i).*(script|javascript|vbscript|onload|onerror).*"),
        Pattern.compile(".*['\";].*")
    };

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(username).matches();
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            return false;
        }
        
        // Check for at least one uppercase, lowercase, digit, and special character
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potential XSS characters
        String sanitized = input
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("/", "&#x2F;");
        
        // Check for SQL injection attempts
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(sanitized).matches()) {
                log.warn("Potential SQL injection attempt detected: {}", input);
                throw new SecurityException("Invalid input detected");
            }
        }
        
        return sanitized.trim();
    }

    public boolean isSafeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return SAFE_STRING_PATTERN.matcher(input).matches();
    }

    public boolean isValidRegion(String region) {
        if (region == null) {
            return false;
        }
        
        String[] validRegions = {"north", "south", "east", "west", "central"};
        for (String validRegion : validRegions) {
            if (validRegion.equals(region.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() != 32) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(apiKey).matches();
    }

    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public void validateStringLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (value.length() < minLength || value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must be between " + minLength + " and " + maxLength + " characters");
        }
    }
}