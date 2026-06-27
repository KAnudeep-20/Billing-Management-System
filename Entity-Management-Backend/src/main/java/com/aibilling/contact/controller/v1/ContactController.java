package com.aibilling.contact.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.contact.dto.ContactCreateRequest;
import com.aibilling.contact.dto.ContactResponse;
import com.aibilling.contact.dto.ContactUpdateRequest;
import com.aibilling.contact.mapper.ContactMapper;
import com.aibilling.contact.model.Contact;
import com.aibilling.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_V1 + "/contacts")
@Tag(name = "Contact Management", description = "Endpoints for managing Contacts linked to Accounts")
public class ContactController {

    private final ContactService service;
    private final ContactMapper mapper;

    public ContactController(ContactService service, ContactMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Contact", description = "Creates a new Contact linked to a specific Account.")
    public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody ContactCreateRequest request) {
        Contact contact = mapper.toEntity(request);
        Contact created = service.create(contact, request.getAccountId(), request.getContactTypeId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Contact", description = "Updates details of an existing Contact.")
    public ResponseEntity<ApiResponse<ContactResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ContactUpdateRequest request) {
        Contact contact = mapper.toEntity(request);
        Contact updated = service.update(id, contact, request.getContactTypeId());
        return ResponseEntity.ok(ApiResponse.success("Contact updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Contact by ID", description = "Retrieves details of a specific Contact by ID.")
    public ResponseEntity<ApiResponse<ContactResponse>> getById(@PathVariable UUID id) {
        Contact contact = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Contact retrieved successfully", mapper.toResponse(contact)));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "List Contacts by Account", description = "Retrieves all active contacts linked to a specific Account.")
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getByAccountId(@PathVariable UUID accountId) {
        List<Contact> contacts = service.findByAccountId(accountId);
        List<ContactResponse> responses = contacts.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Contacts retrieved successfully", responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Contact", description = "Soft deletes a Contact.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Contact deleted successfully", null));
    }
}
