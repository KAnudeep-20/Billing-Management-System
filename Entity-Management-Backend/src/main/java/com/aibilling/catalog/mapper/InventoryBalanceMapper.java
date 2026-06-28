package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.InventoryBalanceResponse;
import com.aibilling.catalog.model.InventoryBalance;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for InventoryBalance domain classes.
 */
@Component
public class InventoryBalanceMapper {

    public InventoryBalanceResponse toResponse(InventoryBalance balance) {
        if (balance == null) return null;
        InventoryBalanceResponse response = new InventoryBalanceResponse();
        response.setId(balance.getId());
        response.setQuantityOnHand(balance.getQuantityOnHand());
        response.setReservedQty(balance.getReservedQty());
        response.setAvailableQty(balance.getAvailableQty());
        response.setStatus(balance.getStatus());
        response.setCreatedAt(balance.getCreatedAt());
        response.setUpdatedAt(balance.getUpdatedAt());

        if (balance.getItem() != null) {
            response.setItemId(balance.getItem().getId());
            response.setItemNumber(balance.getItem().getItemNumber());
            response.setItemName(balance.getItem().getItemName());
        }
        if (balance.getWarehouse() != null) {
            response.setWarehouseId(balance.getWarehouse().getId());
            response.setWarehouseCode(balance.getWarehouse().getCode());
            response.setWarehouseName(balance.getWarehouse().getName());
        }

        return response;
    }
}
