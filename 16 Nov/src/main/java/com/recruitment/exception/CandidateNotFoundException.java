package com.recruitment.exception;

/** Checked exception - see Technical Specification section 11. */
public class CandidateNotFoundException extends Exception {
    public CandidateNotFoundException(String message) {
        super(message);
    }
    public CandidateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
