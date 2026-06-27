package com.aibilling.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Verifies that each exception type is mapped to the correct HTTP status code
 * and that the {@link ErrorResponse} structure is correct.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/v1/entities");
        webRequest = new ServletWebRequest(servletRequest);
    }

    @Test
    @DisplayName("ResourceNotFoundException → 404")
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Entity", "id", "abc-123");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Entity not found"));
        assertEquals("/api/v1/entities", response.getBody().getPath());
    }

    @Test
    @DisplayName("DuplicateResourceException → 409")
    void handleDuplicateResource_returns409() {
        DuplicateResourceException ex =
                new DuplicateResourceException("Entity", "code", "ENT-001");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateResource(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("BusinessException → 422")
    void handleBusinessException_returns422() {
        BusinessException ex = new BusinessException("Cannot deactivate");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex, webRequest);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Cannot deactivate", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unexpected Exception → 500")
    void handleAll_returns500() {
        Exception ex = new RuntimeException("Something broke");

        ResponseEntity<ErrorResponse> response = handler.handleAll(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

}
