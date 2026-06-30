package com.aibilling.ratecard.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.ratecard.dto.EntityRateCardRequest;
import com.aibilling.ratecard.dto.EntityRateCardResponse;
import com.aibilling.ratecard.mapper.EntityRateCardMapper;
import com.aibilling.ratecard.model.EntityRateCard;
import com.aibilling.ratecard.service.EntityRateCardService;
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

@RestController
@RequestMapping(AppConstants.API_V1 + "/entity-assignments")
@Tag(name = "Entity Rate Card Assignment", description = "Endpoints for managing Entity Rate Card Assignments")
public class EntityRateCardController {

    private final EntityRateCardService service;
    private final EntityRateCardMapper mapper;

    public EntityRateCardController(EntityRateCardService service, EntityRateCardMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Assign a Rate Card to an Entity")
    public ResponseEntity<ApiResponse<EntityRateCardResponse>> assign(
            @Valid @RequestBody EntityRateCardRequest request) {
        EntityRateCard erc = service.assignRateCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rate Card assigned to Entity successfully", mapper.toResponse(erc)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace Rate Card Assignment")
    public ResponseEntity<ApiResponse<EntityRateCardResponse>> replace(
            @PathVariable UUID id,
            @Valid @RequestBody EntityRateCardRequest request) {
        EntityRateCard erc = service.replaceAssignment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Rate Card assignment replaced successfully", mapper.toResponse(erc)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove Rate Card Assignment")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable UUID id) {
        service.removeAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Rate Card assignment removed successfully", null));
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Get active assignment for an Entity")
    public ResponseEntity<ApiResponse<EntityRateCardResponse>> getAssigned(@PathVariable UUID entityId) {
        EntityRateCard erc = service.getAssignedRateCard(entityId);
        return ResponseEntity.ok(ApiResponse.success("Active Rate Card assignment retrieved successfully", mapper.toResponse(erc)));
    }

    @GetMapping
    @Operation(summary = "Search assignments (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<EntityRateCardResponse>>> search(
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String rateCardName,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EntityRateCard> pageResult = service.searchAssignments(entityName, rateCardName, pageable);
        PageResponse<EntityRateCardResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Assignments retrieved successfully", response));
    }
}
