package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.RelationshipTypeCreateRequest;
import com.aibilling.setup.dto.RelationshipTypeResponse;
import com.aibilling.setup.dto.RelationshipTypeUpdateRequest;
import com.aibilling.setup.mapper.RelationshipTypeMapper;
import com.aibilling.setup.model.RelationshipType;
import com.aibilling.setup.service.RelationshipTypeService;
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

import java.util.List;
import java.util.UUID;

/**
 * Controller providing REST API endpoints for Relationship Types management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/relationship-types")
@Tag(name = "Relationship Types Management", description = "Endpoints for managing Relationship Types master data")
public class RelationshipTypeController {

    private final RelationshipTypeService service;
    private final RelationshipTypeMapper mapper;

    public RelationshipTypeController(RelationshipTypeService service, RelationshipTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Relationship Type", description = "Creates a Relationship Type. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> create(@Valid @RequestBody RelationshipTypeCreateRequest request) {
        RelationshipType entity = mapper.toEntity(request);
        RelationshipType created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Relationship type created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Relationship Type", description = "Updates a Relationship Type by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RelationshipTypeUpdateRequest request) {
        RelationshipType entity = mapper.toEntity(request);
        RelationshipType updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Relationship type updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Relationship Type by ID", description = "Fetches details of a specific Relationship Type by ID.")
    public ResponseEntity<ApiResponse<RelationshipTypeResponse>> getById(@PathVariable UUID id) {
        RelationshipType entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Relationship type retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Relationship Types (Paginated)", description = "Retrieves all Relationship Types with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<RelationshipTypeResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RelationshipType> pageResult = service.findAll(pageable);
        PageResponse<RelationshipTypeResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Relationship types retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Relationship Types lookup (Dropdowns)", description = "Retrieves a lightweight list of active Relationship Types.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<RelationshipType> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Relationship types lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Relationship Type", description = "Updates status of the Relationship Type to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Relationship type deleted successfully"));
    }

}
