package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.CatalogItemRequest;
import com.aibilling.catalog.dto.CatalogItemResponse;
import com.aibilling.catalog.dto.ItemUomRequest;
import com.aibilling.catalog.dto.ItemUomResponse;
import com.aibilling.catalog.mapper.CatalogItemMapper;
import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.model.ItemUom;
import com.aibilling.catalog.service.CatalogItemService;
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
import java.util.stream.Collectors;

/**
 * Controller providing REST API endpoints for managing Catalog Items and their UOM mappings.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/items")
@Tag(name = "Catalog Item Management", description = "Endpoints for managing Catalog Items and Item UOM mappings")
public class CatalogItemController {

    private final CatalogItemService service;
    private final CatalogItemMapper mapper;

    public CatalogItemController(CatalogItemService service, CatalogItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Catalog Item")
    public ResponseEntity<ApiResponse<CatalogItemResponse>> create(
            @Valid @RequestBody CatalogItemRequest request) {
        CatalogItem item = mapper.toEntity(request);
        CatalogItem created = service.create(item, request.getCategoryId(), request.getPrimaryUomId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catalog item created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Catalog Item")
    public ResponseEntity<ApiResponse<CatalogItemResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody CatalogItemRequest request) {
        CatalogItem item = mapper.toEntity(request);
        CatalogItem updated = service.update(id, item, request.getCategoryId(), request.getPrimaryUomId());
        return ResponseEntity.ok(ApiResponse.success("Catalog item updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Catalog Item by ID")
    public ResponseEntity<ApiResponse<CatalogItemResponse>> getById(@PathVariable UUID id) {
        CatalogItem item = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Catalog item retrieved successfully", mapper.toResponse(item)));
    }

    @GetMapping
    @Operation(summary = "Get all Catalog Items (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<CatalogItemResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CatalogItem> pageResult = service.findAllActive(pageable);
        PageResponse<CatalogItemResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Catalog items retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Catalog Item")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Catalog item deleted successfully", null));
    }

    // ==================== Item UOM Sub-resource ====================

    @PostMapping("/{itemId}/uoms")
    @Operation(summary = "Add a UOM mapping to a Catalog Item")
    public ResponseEntity<ApiResponse<ItemUomResponse>> addItemUom(
            @PathVariable UUID itemId, @Valid @RequestBody ItemUomRequest request) {
        ItemUom created = service.addItemUom(itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item UOM mapping added successfully", toItemUomResponse(created)));
    }

    @PutMapping("/{itemId}/uoms/{itemUomId}")
    @Operation(summary = "Update a UOM mapping of a Catalog Item")
    public ResponseEntity<ApiResponse<ItemUomResponse>> updateItemUom(
            @PathVariable UUID itemId, @PathVariable UUID itemUomId,
            @Valid @RequestBody ItemUomRequest request) {
        ItemUom updated = service.updateItemUom(itemId, itemUomId, request);
        return ResponseEntity.ok(ApiResponse.success("Item UOM mapping updated successfully", toItemUomResponse(updated)));
    }

    @DeleteMapping("/{itemId}/uoms/{itemUomId}")
    @Operation(summary = "Remove a UOM mapping from a Catalog Item")
    public ResponseEntity<ApiResponse<Void>> removeItemUom(
            @PathVariable UUID itemId, @PathVariable UUID itemUomId) {
        service.removeItemUom(itemId, itemUomId);
        return ResponseEntity.ok(ApiResponse.success("Item UOM mapping removed successfully", null));
    }

    @GetMapping("/{itemId}/uoms")
    @Operation(summary = "Get all UOM mappings for a Catalog Item")
    public ResponseEntity<ApiResponse<List<ItemUomResponse>>> getItemUoms(@PathVariable UUID itemId) {
        List<ItemUom> uoms = service.getItemUoms(itemId);
        List<ItemUomResponse> responses = uoms.stream()
                .map(this::toItemUomResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Item UOM mappings retrieved successfully", responses));
    }

    // ==================== Private Helpers ====================

    private ItemUomResponse toItemUomResponse(ItemUom itemUom) {
        ItemUomResponse response = new ItemUomResponse();
        response.setId(itemUom.getId());
        response.setItemId(itemUom.getItem().getId());
        response.setUomId(itemUom.getUom().getId());
        response.setUomCode(itemUom.getUom().getCode());
        response.setConversionFactor(itemUom.getConversionFactor());
        response.setIsDefault(itemUom.getIsDefault());
        response.setStatus(itemUom.getStatus());
        response.setCreatedAt(itemUom.getCreatedAt());
        response.setCreatedBy(itemUom.getCreatedBy());
        response.setUpdatedAt(itemUom.getUpdatedAt());
        response.setUpdatedBy(itemUom.getUpdatedBy());
        return response;
    }
}
