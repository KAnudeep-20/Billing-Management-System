package com.aibilling.account.controller.v1;

import com.aibilling.account.dto.AccountCreateRequest;
import com.aibilling.account.dto.AccountResponse;
import com.aibilling.account.dto.AccountUpdateRequest;
import com.aibilling.account.mapper.AccountMapper;
import com.aibilling.account.model.Account;
import com.aibilling.account.service.AccountService;
import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
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
@RequestMapping(AppConstants.API_V1 + "/accounts")
@Tag(name = "Account Management", description = "Endpoints for managing Accounts linked to Entities")
public class AccountController {

    private final AccountService service;
    private final AccountMapper mapper;

    public AccountController(AccountService service, AccountMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Account", description = "Creates a new Account linked to a specific Entity.")
    public ResponseEntity<ApiResponse<AccountResponse>> create(@Valid @RequestBody AccountCreateRequest request) {
        Account account = mapper.toEntity(request);
        Account created = service.create(account, request.getEntityId(), request.getPaymentTermId(), request.getBillingCycleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an Account", description = "Updates details of an existing Account.")
    public ResponseEntity<ApiResponse<AccountResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AccountUpdateRequest request) {
        Account account = mapper.toEntity(request);
        Account updated = service.update(id, account, request.getPaymentTermId(), request.getBillingCycleId());
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an Account by ID", description = "Retrieves details of a specific Account by ID.")
    public ResponseEntity<ApiResponse<AccountResponse>> getById(@PathVariable UUID id) {
        Account account = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", mapper.toResponse(account)));
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Get Accounts by Entity", description = "Retrieves all active accounts linked to a specific Entity.")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getByEntityId(@PathVariable UUID entityId) {
        List<Account> accounts = service.findByEntityId(entityId);
        List<AccountResponse> responses = accounts.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an Account", description = "Soft deletes an Account. Fails if it is the only remaining active account for the Entity.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
