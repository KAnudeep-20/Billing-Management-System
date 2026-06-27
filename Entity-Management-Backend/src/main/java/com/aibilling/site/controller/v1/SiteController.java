package com.aibilling.site.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.site.dto.SiteCreateRequest;
import com.aibilling.site.dto.SiteResponse;
import com.aibilling.site.dto.SiteUpdateRequest;
import com.aibilling.site.mapper.SiteMapper;
import com.aibilling.site.model.Site;
import com.aibilling.site.service.SiteService;
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
@RequestMapping(AppConstants.API_V1 + "/sites")
@Tag(name = "Site Management", description = "Endpoints for managing Sites linked to Accounts")
public class SiteController {

    private final SiteService service;
    private final SiteMapper mapper;

    public SiteController(SiteService service, SiteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new Site", description = "Creates a new Site linked to a specific Account.")
    public ResponseEntity<ApiResponse<SiteResponse>> create(@Valid @RequestBody SiteCreateRequest request) {
        Site site = mapper.toEntity(request);
        Site created = service.createSite(request.getAccountId(), site, request.getSiteUseIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Site created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Site", description = "Updates details of an existing Site.")
    public ResponseEntity<ApiResponse<SiteResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody SiteUpdateRequest request) {
        Site site = mapper.toEntity(request);
        Site updated = service.updateSite(id, site, request.getSiteUseIds());
        return ResponseEntity.ok(ApiResponse.success("Site updated successfully", mapper.toResponse(updated)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Site by ID", description = "Retrieves details of a specific Site by ID.")
    public ResponseEntity<ApiResponse<SiteResponse>> getById(@PathVariable UUID id) {
        Site site = service.getSiteById(id);
        return ResponseEntity.ok(ApiResponse.success("Site retrieved successfully", mapper.toResponse(site)));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get Sites by Account", description = "Retrieves all active sites linked to a specific Account.")
    public ResponseEntity<ApiResponse<List<SiteResponse>>> getByAccountId(@PathVariable UUID accountId) {
        List<Site> sites = service.getSitesByAccountId(accountId);
        List<SiteResponse> responses = sites.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Sites retrieved successfully", responses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Site", description = "Soft deletes a Site. Fails if it is the only remaining site or only primary site.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.deleteSite(id);
        return ResponseEntity.ok(ApiResponse.success("Site deleted successfully", null));
    }
}
