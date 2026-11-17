package com.recruitment.exception;

/** Checked exception - see Technical Specification section 11. */
public class CandidateNotEligibleException extends Exception {
    public CandidateNotEligibleException(String message) {
        super(message);
    }
    public CandidateNotEligibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
