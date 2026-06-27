package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.BillingCycleCreateRequest;
import com.aibilling.setup.dto.BillingCycleResponse;
import com.aibilling.setup.dto.BillingCycleUpdateRequest;
import com.aibilling.setup.mapper.BillingCycleMapper;
import com.aibilling.setup.model.BillingCycle;
import com.aibilling.setup.service.BillingCycleService;
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
 * Controller providing REST API endpoints for Billing Cycles management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/billing-cycles")
@Tag(name = "Billing Cycles Management", description = "Endpoints for managing Billing Cycles master data")
public class BillingCycleController {

    private final BillingCycleService service;
    private final BillingCycleMapper mapper;

    public BillingCycleController(BillingCycleService service, BillingCycleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Billing Cycle", description = "Creates a Billing Cycle. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<BillingCycleResponse>> create(@Valid @RequestBody BillingCycleCreateRequest request) {
        BillingCycle entity = mapper.toEntity(request);
        BillingCycle created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Billing cycle created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Billing Cycle", description = "Updates a Billing Cycle by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<BillingCycleResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BillingCycleUpdateRequest request) {
        BillingCycle entity = mapper.toEntity(request);
        BillingCycle updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Billing cycle updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Billing Cycle by ID", description = "Fetches details of a specific Billing Cycle by ID.")
    public ResponseEntity<ApiResponse<BillingCycleResponse>> getById(@PathVariable UUID id) {
        BillingCycle entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Billing cycle retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Billing Cycles (Paginated)", description = "Retrieves all Billing Cycles with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<BillingCycleResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BillingCycle> pageResult = service.findAll(pageable);
        PageResponse<BillingCycleResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Billing cycles retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Billing Cycles lookup (Dropdowns)", description = "Retrieves a lightweight list of active Billing Cycles.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<BillingCycle> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Billing cycles lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Billing Cycle", description = "Updates status of the Billing Cycle to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Billing cycle deleted successfully"));
    }

}
