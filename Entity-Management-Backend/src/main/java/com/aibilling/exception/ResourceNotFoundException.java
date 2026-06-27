package com.aibilling.exception;

import lombok.Getter;

/**
 * Thrown when a requested resource cannot be found.
 *
 * <p>Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 *
 * <p>Usage:
 * <pre>
 * throw new ResourceNotFoundException("Entity", "id", "abc-123");
 * // → "Entity not found with id: abc-123"
 * </pre>
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}
