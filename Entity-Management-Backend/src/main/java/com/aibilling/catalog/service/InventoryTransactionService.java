package com.aibilling.catalog.service;

import com.aibilling.catalog.dto.InventoryTransactionRequest;
import com.aibilling.catalog.dto.StockTransferRequest;
import com.aibilling.catalog.model.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing Inventory Transactions.
 */
public interface InventoryTransactionService {

    /**
     * Creates a new inventory transaction and updates the balance.
     */
    InventoryTransaction createTransaction(InventoryTransactionRequest request);

    /**
     * Transfers stock between two warehouses in a single atomic operation.
     */
    void transferStock(StockTransferRequest request);

    /**
     * Retrieves transaction history for an item.
     */
    Page<InventoryTransaction> getTransactionsByItem(UUID itemId, Pageable pageable);

    /**
     * Retrieves transaction history for a warehouse.
     */
    Page<InventoryTransaction> getTransactionsByWarehouse(UUID warehouseId, Pageable pageable);

    /**
     * Retrieves transaction history for an item in a warehouse.
     */
    Page<InventoryTransaction> getTransactionsByItemAndWarehouse(UUID itemId, UUID warehouseId, Pageable pageable);
}
