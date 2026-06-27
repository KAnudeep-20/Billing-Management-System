package com.aibilling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test verifying that the Spring Application Context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class AiBillingApplicationTests {

    @Test
    void contextLoads() {
        // If this test passes, the application context (beans, config, JPA auditing)
        // is wired correctly.
    }

}
