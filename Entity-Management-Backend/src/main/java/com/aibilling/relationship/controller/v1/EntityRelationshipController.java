package com.aibilling.relationship.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.relationship.dto.EntityRelationshipCreateRequest;
import com.aibilling.relationship.dto.EntityRelationshipResponse;
import com.aibilling.relationship.mapper.EntityRelationshipMapper;
import com.aibilling.relationship.model.EntityRelationship;
import com.aibilling.relationship.service.EntityRelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_V1 + "/entity-relationships")
@Tag(name = "Entity Relationship Management", description = "Endpoints for managing relationships between Entities")
public class EntityRelationshipController {

    private final EntityRelationshipService service;
    private final EntityRelationshipMapper mapper;

    public EntityRelationshipController(EntityRelationshipService service, EntityRelationshipMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create an Entity Relationship", description = "Creates a new relationship between two Entities.")
    public ResponseEntity<ApiResponse<EntityRelationshipResponse>> create(@Valid @RequestBody EntityRelationshipCreateRequest request) {
        EntityRelationship created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entity relationship created successfully", mapper.toResponse(created)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an Entity Relationship", description = "Deletes (soft deletes) an existing Entity Relationship.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Entity relationship deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "Query Entity Relationships", description = "Lists active Entity Relationships with optional filtering.")
    public ResponseEntity<ApiResponse<List<EntityRelationshipResponse>>> list(
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID objectId,
            @RequestParam(required = false) UUID entityId) {
        List<EntityRelationship> results = service.listRelationships(subjectId, objectId, entityId);
        return ResponseEntity.ok(ApiResponse.success("Entity relationships retrieved successfully", mapper.toResponseList(results)));
    }
}
