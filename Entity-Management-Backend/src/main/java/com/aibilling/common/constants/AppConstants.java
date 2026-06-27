package com.aibilling.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Application-wide constants shared across all modules.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // ==================== Pagination Defaults ====================
    /** Default page number (zero-indexed). */
    public static final String DEFAULT_PAGE_NUMBER = "0";

    /** Default page size. */
    public static final String DEFAULT_PAGE_SIZE = "20";

    /** Maximum allowed page size to prevent unbounded queries. */
    public static final int MAX_PAGE_SIZE = 100;

    /** Default sort field. */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** Default sort direction. */
    public static final String DEFAULT_SORT_DIR = "desc";

    // ==================== API Versioning ====================
    /** Base path for API v1 endpoints. */
    public static final String API_V1 = "/v1";

    // ==================== Audit ====================
    /** Default system user for audit fields when no user is authenticated. */
    public static final String SYSTEM_USER = "SYSTEM";

}
