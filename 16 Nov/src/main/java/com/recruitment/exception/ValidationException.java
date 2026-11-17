package com.recruitment.exception;

/** Thrown when input data fails a business validation (bad %, negative backlog, ...). */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
