package com.aibilling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Provides:
 * <ul>
 *   <li>API metadata (title, version, description)</li>
 *   <li>Grouped API endpoints by version prefix</li>
 * </ul>
 *
 * <p>Swagger UI is accessible at: {@code /api/swagger-ui/index.html}
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Entity Management API")
                        .version("1.0.0")
                        .description("Backend API for Entity Management — AI Billing Platform. "
                                + "Supports Entity, Account, Site, Contact management and relationships.")
                        .contact(new Contact()
                                .name("AI Billing Team")
                                .email("team@aibilling.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://aibilling.com")))
                .servers(List.of(
                        new Server().url("/api").description("Default Server")));
    }

    /**
     * Groups all v1 endpoints under a single Swagger group.
     */
    @Bean
    public GroupedOpenApi apiV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("API v1")
                .pathsToMatch("/**")
                .build();
    }

}
