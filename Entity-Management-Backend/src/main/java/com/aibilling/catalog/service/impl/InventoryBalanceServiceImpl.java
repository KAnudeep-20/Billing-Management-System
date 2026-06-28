package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.model.InventoryBalance;
import com.aibilling.catalog.repository.InventoryBalanceRepository;
import com.aibilling.catalog.service.InventoryBalanceService;
import com.aibilling.common.enums.Status;
import com.aibilling.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for viewing Inventory Balances (read-only).
 */
@Service
@Transactional(readOnly = true)
public class InventoryBalanceServiceImpl implements InventoryBalanceService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryBalanceServiceImpl.class);

    private final InventoryBalanceRepository balanceRepository;

    public InventoryBalanceServiceImpl(InventoryBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public InventoryBalance getBalance(UUID itemId, UUID warehouseId) {
        return balanceRepository.findByItemIdAndWarehouseIdAndStatus(itemId, warehouseId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryBalance",
                        "itemId/warehouseId", itemId + "/" + warehouseId));
    }

    @Override
    public List<InventoryBalance> getBalancesByItem(UUID itemId) {
        return balanceRepository.findByItemIdAndStatus(itemId, Status.ACTIVE);
    }

    @Override
    public List<InventoryBalance> getBalancesByWarehouse(UUID warehouseId) {
        return balanceRepository.findByWarehouseIdAndStatus(warehouseId, Status.ACTIVE);
    }
}
