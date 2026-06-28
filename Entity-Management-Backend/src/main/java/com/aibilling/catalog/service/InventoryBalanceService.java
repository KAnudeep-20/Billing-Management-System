package com.aibilling.catalog.service;

import com.aibilling.catalog.model.InventoryBalance;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for viewing Inventory Balances.
 * Balances are read-only — updates flow only through Inventory Transactions.
 */
public interface InventoryBalanceService {

    /**
     * Retrieves the balance for a specific item in a specific warehouse.
     */
    InventoryBalance getBalance(UUID itemId, UUID warehouseId);

    /**
     * Retrieves all balances for a specific item across all warehouses.
     */
    List<InventoryBalance> getBalancesByItem(UUID itemId);

    /**
     * Retrieves all balances for a specific warehouse.
     */
    List<InventoryBalance> getBalancesByWarehouse(UUID warehouseId);
}
