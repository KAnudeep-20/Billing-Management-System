package com.aibilling.entity.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.entity.dto.EntityCreateRequest;
import com.aibilling.entity.dto.EntityResponse;
import com.aibilling.entity.dto.EntityUpdateRequest;
import com.aibilling.entity.mapper.EntityMapper;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.service.EntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller providing REST API endpoints for managing Entity aggregate roots.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/entities")
@Tag(name = "Entity Management", description = "Endpoints for managing core Entities (Organizations and Persons)")
public class EntityController {

    private final EntityService service;
    private final EntityMapper mapper;
    private final com.aibilling.entity.service.EntityAggregationService aggregationService;

    public EntityController(EntityService service, EntityMapper mapper, com.aibilling.entity.service.EntityAggregationService aggregationService) {
        this.service = service;
        this.mapper = mapper;
        this.aggregationService = aggregationService;
    }

    @GetMapping("/{id}/complete")
    @Operation(summary = "Get Complete Entity Details Hierarchy", description = "Retrieves Entity Summary, Accounts, Sites, Contacts, and Relationships efficiently.")
    public ResponseEntity<ApiResponse<com.aibilling.entity.dto.CompleteEntityDetailsResponse>> getCompleteDetails(@PathVariable UUID id) {
        com.aibilling.entity.dto.CompleteEntityDetailsResponse response = aggregationService.getCompleteEntityDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Complete Entity Details retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new Entity", description = "Creates a core entity which can represent a Person or an Organization.")
    public ResponseEntity<ApiResponse<EntityResponse>> create(@Valid @RequestBody EntityCreateRequest request) {
        Entity entity = mapper.toEntity(request);
        Entity created = service.create(entity, request.getEntityTypeCodes());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entity created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Entity", description = "Updates core entity details and role types by ID.")
    public ResponseEntity<ApiResponse<EntityResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody EntityUpdateRequest request) {
        Entity entity = mapper.toEntity(request);
        Entity updated = service.update(id, entity, request.getEntityTypeCodes());
        return ResponseEntity.ok(ApiResponse.success("Entity updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an Entity by ID", description = "Retrieves full details of a specific Entity by ID.")
    public ResponseEntity<ApiResponse<EntityResponse>> getById(@PathVariable UUID id) {
        Entity entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Entity retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Entities (Paginated)", description = "Retrieves all Entities with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<EntityResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Entity> pageResult = service.findAll(pageable);
        PageResponse<EntityResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Entities retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an Entity", description = "Updates status of the Entity to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Entity deleted successfully", null));
    }

}
