package com.aibilling.common.enums;

/**
 * Represents the lifecycle status of a domain entity.
 *
 * <p>Used by {@link com.aibilling.audit.BaseEntity} to support soft-delete
 * and activation/deactivation workflows.
 */
public enum Status {

    /** The entity is active and fully operational. */
    ACTIVE,

    /** The entity has been deactivated but not deleted. */
    INACTIVE,

    /** The entity has been soft-deleted. */
    DELETED

}
