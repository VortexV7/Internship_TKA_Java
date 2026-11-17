package com.recruitment.exception;

/** Checked exception - see Technical Specification section 11. */
public class JobNotFoundException extends Exception {
    public JobNotFoundException(String message) {
        super(message);
    }
    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
