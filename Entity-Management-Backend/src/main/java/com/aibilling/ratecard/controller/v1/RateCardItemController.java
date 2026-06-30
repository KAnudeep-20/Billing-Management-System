package com.aibilling.ratecard.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.ratecard.dto.RateCardItemRequest;
import com.aibilling.ratecard.dto.RateCardItemResponse;
import com.aibilling.ratecard.dto.RateCardItemUpdateRequest;
import com.aibilling.ratecard.mapper.RateCardItemMapper;
import com.aibilling.ratecard.model.RateCardItem;
import com.aibilling.ratecard.service.RateCardItemService;
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

@RestController
@RequestMapping(AppConstants.API_V1 + "/rate-cards/{rateCardId}/items")
@Tag(name = "Rate Card Item Management", description = "Endpoints for managing Rate Card Items")
public class RateCardItemController {

    private final RateCardItemService service;
    private final RateCardItemMapper mapper;

    public RateCardItemController(RateCardItemService service, RateCardItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Add an item to a Rate Card")
    public ResponseEntity<ApiResponse<RateCardItemResponse>> addItem(
            @PathVariable UUID rateCardId,
            @Valid @RequestBody RateCardItemRequest request) {
        RateCardItem item = service.addItem(rateCardId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to Rate Card successfully", mapper.toResponse(item)));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update an item price in a Rate Card")
    public ResponseEntity<ApiResponse<RateCardItemResponse>> updatePrice(
            @PathVariable UUID rateCardId,
            @PathVariable UUID itemId,
            @Valid @RequestBody RateCardItemUpdateRequest request) {
        RateCardItem item = service.updatePrice(rateCardId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Item price updated successfully", mapper.toResponse(item)));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Remove an item from a Rate Card")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable UUID rateCardId,
            @PathVariable UUID itemId) {
        service.removeItem(rateCardId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from Rate Card successfully", null));
    }

    @GetMapping
    @Operation(summary = "List all active items in a Rate Card")
    public ResponseEntity<ApiResponse<List<RateCardItemResponse>>> getItems(@PathVariable UUID rateCardId) {
        List<RateCardItem> items = service.getItems(rateCardId);
        List<RateCardItemResponse> response = items.stream().map(mapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Items retrieved successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search items in a Rate Card (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<RateCardItemResponse>>> searchItems(
            @PathVariable UUID rateCardId,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RateCardItem> pageResult = service.searchItems(rateCardId, itemCode, itemName, pageable);
        PageResponse<RateCardItemResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Items retrieved successfully", response));
    }
}
