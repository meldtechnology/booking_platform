package com.jade.platform.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * 
 * @author Jade Platform Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * @param message the detail message
     * @param cause the underlying cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a ResourceNotFoundException for a resource with the given ID.
     *
     * @param resourceType the type of resource
     * @param id the ID of the resource
     * @return a new ResourceNotFoundException with an appropriate error message
     */
    public static ResourceNotFoundException forId(String resourceType, Object id) {
        if ("Catalog".equals(resourceType)) {
            // Fix: Using the single-parameter constructor instead of trying to pass an Object array
            return new ResourceNotFoundException("error.resource.notfound.catalog");
        }
        return new ResourceNotFoundException(resourceType + " not found with ID: " + id);
    }
}