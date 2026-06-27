package com.aibilling.exception;

import lombok.Getter;

/**
 * Thrown when a business rule violation occurs.
 *
 * <p>Mapped to HTTP 422 (Unprocessable Entity) by {@link GlobalExceptionHandler}.
 *
 * <p>Usage:
 * <pre>
 * throw new BusinessException("Cannot deactivate entity with active child accounts");
 * </pre>
 */
@Getter
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
