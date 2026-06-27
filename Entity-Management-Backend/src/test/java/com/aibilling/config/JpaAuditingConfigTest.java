package com.aibilling.config;

import com.aibilling.common.constants.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link JpaAuditingConfig}.
 */
@SpringBootTest
@ActiveProfiles("test")
class JpaAuditingConfigTest {

    @Autowired
    private AuditorAware<String> auditorAware;

    @Test
    @DisplayName("AuditorAware bean is registered and returns SYSTEM")
    void auditorAware_returnsSYSTEM() {
        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertTrue(auditor.isPresent());
        assertEquals(AppConstants.SYSTEM_USER, auditor.get());
    }

}
