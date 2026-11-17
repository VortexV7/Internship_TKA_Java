package com.recruitment.util;

import com.recruitment.exception.ValidationException;

/** Small reusable input validations (Spec sections 5 and 29). */
public class ValidationUtil {

    public static void requireNotBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void checkPercentage(double percentage) throws ValidationException {
        if (percentage < 0 || percentage > 100) {
            throw new ValidationException(
                    "Percentage must be between 0 and 100 (got " + percentage + ").");
        }
    }

    public static void checkBacklogs(int backlogCount) throws ValidationException {
        if (backlogCount < 0) {
            throw new ValidationException("Backlog count cannot be negative.");
        }
    }

    public static void checkOpenings(int openings) throws ValidationException {
        if (openings <= 0) {
            throw new ValidationException("Openings must be greater than zero.");
        }
    }

    public static void checkPackageRange(double minPackage, double maxPackage)
            throws ValidationException {
        if (minPackage > maxPackage) {
            throw new ValidationException(
                    "Minimum package (" + minPackage + ") must not exceed maximum package (" + maxPackage + ").");
        }
    }

    public static void checkRating(double rating, String fieldName) throws ValidationException {
        if (rating < 0 || rating > 5) {
            throw new ValidationException(fieldName + " rating must be between 0 and 5.");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }

    public static boolean isValidMobile(String mobile) {
        return mobile != null && mobile.matches("[0-9]{10}");
    }
}
