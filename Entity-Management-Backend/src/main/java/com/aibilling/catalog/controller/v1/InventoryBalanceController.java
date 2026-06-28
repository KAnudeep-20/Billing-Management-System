package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.InventoryBalanceResponse;
import com.aibilling.catalog.mapper.InventoryBalanceMapper;
import com.aibilling.catalog.model.InventoryBalance;
import com.aibilling.catalog.service.InventoryBalanceService;
import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller providing REST API endpoints for viewing Inventory Balances (read-only).
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/inventory/balances")
@Tag(name = "Inventory Balance", description = "Endpoints for viewing Inventory Balances")
public class InventoryBalanceController {

    private final InventoryBalanceService service;
    private final InventoryBalanceMapper mapper;

    public InventoryBalanceController(InventoryBalanceService service, InventoryBalanceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Get Inventory Balance", description = "Query by itemId, warehouseId, or both.")
    public ResponseEntity<ApiResponse<List<InventoryBalanceResponse>>> getBalances(
            @RequestParam(required = false) UUID itemId,
            @RequestParam(required = false) UUID warehouseId) {

        List<InventoryBalance> balances;

        if (itemId != null && warehouseId != null) {
            InventoryBalance balance = service.getBalance(itemId, warehouseId);
            balances = List.of(balance);
        } else if (itemId != null) {
            balances = service.getBalancesByItem(itemId);
        } else if (warehouseId != null) {
            balances = service.getBalancesByWarehouse(warehouseId);
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("At least one of itemId or warehouseId is required"));
        }

        List<InventoryBalanceResponse> responses = balances.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Inventory balances retrieved successfully", responses));
    }
}
