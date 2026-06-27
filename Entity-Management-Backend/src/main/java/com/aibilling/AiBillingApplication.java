package com.aibilling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Entity Management Backend application.
 *
 * <p>This application provides the backend foundation for:
 * <ul>
 *   <li>Entity Management</li>
 *   <li>Account Management</li>
 *   <li>Site Management</li>
 *   <li>Contact Management</li>
 *   <li>Entity Relationships</li>
 *   <li>Setup / Master Data</li>
 * </ul>
 */
@SpringBootApplication
public class AiBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiBillingApplication.class, args);
    }

}
