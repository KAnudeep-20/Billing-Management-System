package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.EntityTypeCreateRequest;
import com.aibilling.setup.dto.EntityTypeResponse;
import com.aibilling.setup.dto.EntityTypeUpdateRequest;
import com.aibilling.setup.mapper.EntityTypeMapper;
import com.aibilling.setup.model.EntityType;
import com.aibilling.setup.service.EntityTypeService;
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
 * Controller providing REST API endpoints for Entity Types management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/entity-types")
@Tag(name = "Entity Types Management", description = "Endpoints for managing Entity Types master data")
public class EntityTypeController {

    private final EntityTypeService service;
    private final EntityTypeMapper mapper;

    public EntityTypeController(EntityTypeService service, EntityTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Entity Type", description = "Creates an Entity Type. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<EntityTypeResponse>> create(@Valid @RequestBody EntityTypeCreateRequest request) {
        EntityType entity = mapper.toEntity(request);
        EntityType created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entity type created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Entity Type", description = "Updates an Entity Type by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<EntityTypeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody EntityTypeUpdateRequest request) {
        EntityType entity = mapper.toEntity(request);
        EntityType updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Entity type updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an Entity Type by ID", description = "Fetches details of a specific Entity Type by ID.")
    public ResponseEntity<ApiResponse<EntityTypeResponse>> getById(@PathVariable UUID id) {
        EntityType entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Entity type retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Entity Types (Paginated)", description = "Retrieves all Entity Types with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<EntityTypeResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EntityType> pageResult = service.findAll(pageable);
        PageResponse<EntityTypeResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Entity types retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Entity Types lookup (Dropdowns)", description = "Retrieves a lightweight list of active Entity Types.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<EntityType> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Entity types lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an Entity Type", description = "Updates status of the Entity Type to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Entity type deleted successfully"));
    }

}
