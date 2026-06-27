package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.SiteUseCreateRequest;
import com.aibilling.setup.dto.SiteUseResponse;
import com.aibilling.setup.dto.SiteUseUpdateRequest;
import com.aibilling.setup.mapper.SiteUseMapper;
import com.aibilling.setup.model.SiteUse;
import com.aibilling.setup.service.SiteUseService;
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
 * Controller providing REST API endpoints for Site Uses management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/site-uses")
@Tag(name = "Site Uses Management", description = "Endpoints for managing Site Uses master data")
public class SiteUseController {

    private final SiteUseService service;
    private final SiteUseMapper mapper;

    public SiteUseController(SiteUseService service, SiteUseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Site Use", description = "Creates a Site Use. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<SiteUseResponse>> create(@Valid @RequestBody SiteUseCreateRequest request) {
        SiteUse entity = mapper.toEntity(request);
        SiteUse created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Site use created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Site Use", description = "Updates a Site Use by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<SiteUseResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody SiteUseUpdateRequest request) {
        SiteUse entity = mapper.toEntity(request);
        SiteUse updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Site use updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Site Use by ID", description = "Fetches details of a specific Site Use by ID.")
    public ResponseEntity<ApiResponse<SiteUseResponse>> getById(@PathVariable UUID id) {
        SiteUse entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Site use retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Site Uses (Paginated)", description = "Retrieves all Site Uses with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<SiteUseResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SiteUse> pageResult = service.findAll(pageable);
        PageResponse<SiteUseResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Site uses retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Site Uses lookup (Dropdowns)", description = "Retrieves a lightweight list of active Site Uses.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<SiteUse> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Site uses lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Site Use", description = "Updates status of the Site Use to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Site use deleted successfully"));
    }

}
