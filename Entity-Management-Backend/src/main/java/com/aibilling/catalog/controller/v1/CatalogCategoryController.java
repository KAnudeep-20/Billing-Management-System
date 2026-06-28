package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.CatalogCategoryRequest;
import com.aibilling.catalog.dto.CatalogCategoryResponse;
import com.aibilling.catalog.dto.CatalogCategoryTreeResponse;
import com.aibilling.catalog.mapper.CatalogCategoryMapper;
import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.catalog.service.CatalogCategoryService;
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

import java.util.List;
import java.util.UUID;

/**
 * Controller providing REST API endpoints for managing Catalog Categories.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/categories")
@Tag(name = "Catalog Category Management", description = "Endpoints for managing Catalog Categories")
public class CatalogCategoryController {

    private final CatalogCategoryService service;
    private final CatalogCategoryMapper mapper;

    public CatalogCategoryController(CatalogCategoryService service, CatalogCategoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Catalog Category")
    public ResponseEntity<ApiResponse<CatalogCategoryResponse>> create(
            @Valid @RequestBody CatalogCategoryRequest request) {
        CatalogCategory category = mapper.toEntity(request);
        CatalogCategory created = service.create(category, request.getParentCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Catalog Category")
    public ResponseEntity<ApiResponse<CatalogCategoryResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody CatalogCategoryRequest request) {
        CatalogCategory category = mapper.toEntity(request);
        CatalogCategory updated = service.update(id, category, request.getParentCategoryId());
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Catalog Category by ID")
    public ResponseEntity<ApiResponse<CatalogCategoryResponse>> getById(@PathVariable UUID id) {
        CatalogCategory category = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", mapper.toResponse(category)));
    }

    @GetMapping
    @Operation(summary = "Get all Catalog Categories (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<CatalogCategoryResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CatalogCategory> pageResult = service.findAllActive(pageable);
        PageResponse<CatalogCategoryResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", response));
    }

    @GetMapping("/tree")
    @Operation(summary = "Get Category Hierarchy Tree", description = "Returns the complete category tree structure.")
    public ResponseEntity<ApiResponse<List<CatalogCategoryTreeResponse>>> getCategoryTree() {
        List<CatalogCategoryTreeResponse> tree = service.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.success("Category tree retrieved successfully", tree));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Catalog Category")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
