package com.recruitment.exception;

/** Checked exception - see Technical Specification section 11. */
public class DuplicateApplicationException extends Exception {
    public DuplicateApplicationException(String message) {
        super(message);
    }
    public DuplicateApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
