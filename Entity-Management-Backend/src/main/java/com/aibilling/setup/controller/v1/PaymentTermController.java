package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.PaymentTermCreateRequest;
import com.aibilling.setup.dto.PaymentTermResponse;
import com.aibilling.setup.dto.PaymentTermUpdateRequest;
import com.aibilling.setup.mapper.PaymentTermMapper;
import com.aibilling.setup.model.PaymentTerm;
import com.aibilling.setup.service.PaymentTermService;
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
 * Controller providing REST API endpoints for Payment Terms management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/payment-terms")
@Tag(name = "Payment Terms Management", description = "Endpoints for managing Payment Terms master data")
public class PaymentTermController {

    private final PaymentTermService service;
    private final PaymentTermMapper mapper;

    public PaymentTermController(PaymentTermService service, PaymentTermMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Payment Term", description = "Creates a Payment Term. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<PaymentTermResponse>> create(@Valid @RequestBody PaymentTermCreateRequest request) {
        PaymentTerm entity = mapper.toEntity(request);
        PaymentTerm created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment term created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Payment Term", description = "Updates a Payment Term by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<PaymentTermResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentTermUpdateRequest request) {
        PaymentTerm entity = mapper.toEntity(request);
        PaymentTerm updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Payment term updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Payment Term by ID", description = "Fetches details of a specific Payment Term by ID.")
    public ResponseEntity<ApiResponse<PaymentTermResponse>> getById(@PathVariable UUID id) {
        PaymentTerm entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Payment term retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Payment Terms (Paginated)", description = "Retrieves all Payment Terms with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<PaymentTermResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PaymentTerm> pageResult = service.findAll(pageable);
        PageResponse<PaymentTermResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Payment terms retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Payment Terms lookup (Dropdowns)", description = "Retrieves a lightweight list of active Payment Terms.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<PaymentTerm> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Payment terms lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Payment Term", description = "Updates status of the Payment Term to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Payment term deleted successfully"));
    }

}
