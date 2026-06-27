package com.aibilling.exception;

import lombok.Getter;

/**
 * Thrown when an attempt is made to create a resource that already exists.
 *
 * <p>Mapped to HTTP 409 (Conflict) by {@link GlobalExceptionHandler}.
 *
 * <p>Usage:
 * <pre>
 * throw new DuplicateResourceException("Entity", "code", "ENT-001");
 * // → "Entity already exists with code: ENT-001"
 * </pre>
 */
@Getter
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}
