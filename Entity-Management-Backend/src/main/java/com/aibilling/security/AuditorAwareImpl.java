package com.aibilling.security;

import com.aibilling.common.constants.AppConstants;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Provides the current auditor (user) for JPA auditing.
 *
 * <p>This stub implementation always returns {@code "SYSTEM"}.
 * Replace with an actual security-context-based implementation
 * when Spring Security is integrated.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Returns the current auditor.
     *
     * @return {@code "SYSTEM"} — stub value until authentication is added.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Replace with SecurityContextHolder-based lookup
        //       when Spring Security is integrated.
        return Optional.of(AppConstants.SYSTEM_USER);
    }

}
