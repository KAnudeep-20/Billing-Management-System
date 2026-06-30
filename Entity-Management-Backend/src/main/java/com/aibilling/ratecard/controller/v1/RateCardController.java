package com.aibilling.ratecard.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.common.enums.Status;
import com.aibilling.ratecard.dto.RateCardCreateRequest;
import com.aibilling.ratecard.dto.RateCardResponse;
import com.aibilling.ratecard.dto.RateCardUpdateRequest;
import com.aibilling.ratecard.mapper.RateCardMapper;
import com.aibilling.ratecard.model.RateCard;
import com.aibilling.ratecard.service.RateCardService;
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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_V1 + "/rate-cards")
@Tag(name = "Rate Card Management", description = "Endpoints for managing Rate Cards")
public class RateCardController {

    private final RateCardService service;
    private final RateCardMapper mapper;

    public RateCardController(RateCardService service, RateCardMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Rate Card")
    public ResponseEntity<ApiResponse<RateCardResponse>> create(@Valid @RequestBody RateCardCreateRequest request) {
        RateCard rc = mapper.toEntity(request);
        RateCard created = service.create(rc);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rate Card created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Rate Card")
    public ResponseEntity<ApiResponse<RateCardResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RateCardUpdateRequest request) {
        RateCard rc = mapper.toEntity(request);
        RateCard updated = service.update(id, rc);
        return ResponseEntity.ok(ApiResponse.success("Rate Card updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Rate Card by ID")
    public ResponseEntity<ApiResponse<RateCardResponse>> getById(@PathVariable UUID id) {
        RateCard rc = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Rate Card retrieved successfully", mapper.toResponse(rc)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Rate Card")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Rate Card deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "Search Rate Cards (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<RateCardResponse>>> search(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RateCard> pageResult = service.search(code, name, status, pageable);
        PageResponse<RateCardResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Rate Cards retrieved successfully", response));
    }

    @GetMapping("/{id}/price")
    @Operation(summary = "Get price by Rate Card and Catalog Item")
    public ResponseEntity<ApiResponse<BigDecimal>> getPriceByRateCard(
            @PathVariable("id") UUID rateCardId,
            @RequestParam UUID catalogItemId) {
        BigDecimal price = service.getPriceByRateCard(rateCardId, catalogItemId);
        return ResponseEntity.ok(ApiResponse.success("Price retrieved successfully", price));
    }

    @GetMapping("/price-by-entity")
    @Operation(summary = "Get price by Entity and Catalog Item")
    public ResponseEntity<ApiResponse<BigDecimal>> getPriceByEntity(
            @RequestParam UUID entityId,
            @RequestParam UUID catalogItemId) {
        BigDecimal price = service.getPriceByEntity(entityId, catalogItemId);
        return ResponseEntity.ok(ApiResponse.success("Price retrieved successfully", price));
    }

    @GetMapping("/price-by-catalog-item")
    @Operation(summary = "Get list price by Catalog Item")
    public ResponseEntity<ApiResponse<BigDecimal>> getPriceByCatalogItem(
            @RequestParam UUID catalogItemId) {
        BigDecimal price = service.getPriceByCatalogItem(catalogItemId);
        return ResponseEntity.ok(ApiResponse.success("Price retrieved successfully", price));
    }
}
