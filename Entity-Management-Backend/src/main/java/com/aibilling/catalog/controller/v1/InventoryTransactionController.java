package com.aibilling.catalog.controller.v1;

import com.aibilling.catalog.dto.InventoryTransactionRequest;
import com.aibilling.catalog.dto.InventoryTransactionResponse;
import com.aibilling.catalog.dto.StockTransferRequest;
import com.aibilling.catalog.mapper.InventoryTransactionMapper;
import com.aibilling.catalog.model.InventoryTransaction;
import com.aibilling.catalog.service.InventoryTransactionService;
import com.aibilling.common.constants.AppConstants;
import com.aibilling.common.dto.ApiResponse;
import com.aibilling.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller providing REST API endpoints for Inventory Transactions.
 */
@RestController
@RequestMapping(AppConstants.API_V1 + "/inventory/transactions")
@Tag(name = "Inventory Transactions", description = "Endpoints for creating and querying Inventory Transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService service;
    private final InventoryTransactionMapper mapper;

    public InventoryTransactionController(InventoryTransactionService service, InventoryTransactionMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create an Inventory Transaction", description = "Records a ledger entry and updates the inventory balance.")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> create(
            @Valid @RequestBody InventoryTransactionRequest request) {
        InventoryTransaction txn = service.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory transaction created successfully", mapper.toResponse(txn)));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer Stock Between Warehouses",
            description = "Records TRANSFER_OUT and TRANSFER_IN transactions atomically.")
    public ResponseEntity<ApiResponse<Void>> transferStock(
            @Valid @RequestBody StockTransferRequest request) {
        service.transferStock(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock transfer completed successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get Inventory Transaction History",
            description = "Query by itemId, warehouseId, or both.")
    public ResponseEntity<ApiResponse<PageResponse<InventoryTransactionResponse>>> getTransactions(
            @RequestParam(required = false) UUID itemId,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIR) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InventoryTransaction> pageResult;
        if (itemId != null && warehouseId != null) {
            pageResult = service.getTransactionsByItemAndWarehouse(itemId, warehouseId, pageable);
        } else if (itemId != null) {
            pageResult = service.getTransactionsByItem(itemId, pageable);
        } else if (warehouseId != null) {
            pageResult = service.getTransactionsByWarehouse(warehouseId, pageable);
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("At least one of itemId or warehouseId is required"));
        }

        PageResponse<InventoryTransactionResponse> response = PageResponse.from(
                pageResult.map(mapper::toResponse));
        return ResponseEntity.ok(ApiResponse.success("Inventory transactions retrieved successfully", response));
    }
}
