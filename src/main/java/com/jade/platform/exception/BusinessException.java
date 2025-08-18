package com.jade.platform.exception;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessException extends RuntimeException {
    
    /**
     * Constructs a new BusinessException with the specified detail message.
     * 
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new BusinessException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause of this exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}