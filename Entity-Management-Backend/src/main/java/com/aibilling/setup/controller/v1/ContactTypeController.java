package com.aibilling.setup.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.ContactTypeCreateRequest;
import com.aibilling.setup.dto.ContactTypeResponse;
import com.aibilling.setup.dto.ContactTypeUpdateRequest;
import com.aibilling.setup.mapper.ContactTypeMapper;
import com.aibilling.setup.model.ContactType;
import com.aibilling.setup.service.ContactTypeService;
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
 * Controller providing REST API endpoints for Contact Types management.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/contact-types")
@Tag(name = "Contact Types Management", description = "Endpoints for managing Contact Types master data")
public class ContactTypeController {

    private final ContactTypeService service;
    private final ContactTypeMapper mapper;

    public ContactTypeController(ContactTypeService service, ContactTypeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Contact Type", description = "Creates a Contact Type. Code and Name must be unique.")
    public ResponseEntity<ApiResponse<ContactTypeResponse>> create(@Valid @RequestBody ContactTypeCreateRequest request) {
        ContactType entity = mapper.toEntity(request);
        ContactType created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact type created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing Contact Type", description = "Updates a Contact Type by ID. Code and Name uniqueness are validated.")
    public ResponseEntity<ApiResponse<ContactTypeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ContactTypeUpdateRequest request) {
        ContactType entity = mapper.toEntity(request);
        ContactType updated = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success("Contact type updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Contact Type by ID", description = "Fetches details of a specific Contact Type by ID.")
    public ResponseEntity<ApiResponse<ContactTypeResponse>> getById(@PathVariable UUID id) {
        ContactType entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Contact type retrieved successfully", mapper.toResponse(entity)));
    }

    @GetMapping
    @Operation(summary = "Get all Contact Types (Paginated)", description = "Retrieves all Contact Types with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<ContactTypeResponse>>> getAll(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ContactType> pageResult = service.findAll(pageable);
        PageResponse<ContactTypeResponse> response = PageResponse.from(pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Contact types retrieved successfully", response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Get active Contact Types lookup (Dropdowns)", description = "Retrieves a lightweight list of active Contact Types.")
    public ResponseEntity<ApiResponse<List<LookupResponse>>> lookup() {
        List<ContactType> activeList = service.findAllActive();
        List<LookupResponse> lookupResponse = mapper.toLookupResponseList(activeList);
        return ResponseEntity.ok(ApiResponse.success("Contact types lookup retrieved successfully", lookupResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a Contact Type", description = "Updates status of the Contact Type to DELETED.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Contact type deleted successfully"));
    }

}
