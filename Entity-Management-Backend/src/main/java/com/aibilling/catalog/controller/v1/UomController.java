package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.UomRequest;
import com.aibilling.catalog.dto.UomResponse;
import com.aibilling.catalog.mapper.UomMapper;
import com.aibilling.catalog.model.Uom;
import com.aibilling.catalog.service.UomService;
import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * Controller providing REST API endpoints for managing Units of Measure.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/uoms")
@Tag(name = "UOM Management", description = "Endpoints for managing Units of Measure")
public class UomController {

    private final UomService service;
    private final UomMapper mapper;

    public UomController(UomService service, UomMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new UOM")
    public ResponseEntity<ApiResponse<UomResponse>> create(@Valid @RequestBody UomRequest request) {
        Uom uom = mapper.toEntity(request);
        Uom created = service.create(uom);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("UOM created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing UOM")
    public ResponseEntity<ApiResponse<UomResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UomRequest request) {
        Uom uom = mapper.toEntity(request);
        Uom updated = service.update(id, uom);
        return ResponseEntity.ok(ApiResponse.success("UOM updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a UOM by ID")
    public ResponseEntity<ApiResponse<UomResponse>> getById(@PathVariable UUID id) {
        Uom uom = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("UOM retrieved successfully", mapper.toResponse(uom)));
    }

    @GetMapping
    @Operation(summary = "Get all UOMs (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<UomResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Uom> pageResult = service.findAllActive(pageable);
        PageResponse<UomResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("UOMs retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a UOM")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("UOM deleted successfully", null));
    }
}
