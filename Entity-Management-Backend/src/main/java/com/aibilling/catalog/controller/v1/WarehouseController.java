package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.WarehouseRequest;
import com.aibilling.catalog.dto.WarehouseResponse;
import com.aibilling.catalog.mapper.WarehouseMapper;
import com.aibilling.catalog.model.Warehouse;
import com.aibilling.catalog.service.WarehouseService;
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
 * Controller providing REST API endpoints for managing Warehouses.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/warehouses")
@Tag(name = "Warehouse Management", description = "Endpoints for managing Warehouses")
public class WarehouseController {

    private final WarehouseService service;
    private final WarehouseMapper mapper;

    public WarehouseController(WarehouseService service, WarehouseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(
            @Valid @RequestBody WarehouseRequest request) {
        Warehouse warehouse = mapper.toEntity(request);
        Warehouse created = service.create(warehouse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Warehouse created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody WarehouseRequest request) {
        Warehouse warehouse = mapper.toEntity(request);
        Warehouse updated = service.update(id, warehouse);
        return ResponseEntity.ok(ApiResponse.success("Warehouse updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getById(@PathVariable UUID id) {
        Warehouse warehouse = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse retrieved successfully", mapper.toResponse(warehouse)));
    }

    @GetMapping
    @Operation(summary = "Get all Warehouses (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<WarehouseResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Warehouse> pageResult = service.findAllActive(pageable);
        PageResponse<WarehouseResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Warehouses retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Warehouse")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse deleted successfully", null));
    }
}
