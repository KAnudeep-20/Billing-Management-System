package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.InventoryTransactionResponse;
import com.aibilling.catalog.model.InventoryTransaction;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for InventoryTransaction domain classes.
 */
@Component
public class InventoryTransactionMapper {

    public InventoryTransactionResponse toResponse(InventoryTransaction txn) {
        if (txn == null) return null;
        InventoryTransactionResponse response = new InventoryTransactionResponse();
        response.setId(txn.getId());
        response.setTransactionDate(txn.getTransactionDate());
        response.setTransactionType(txn.getTransactionType());
        response.setReferenceType(txn.getReferenceType());
        response.setReferenceId(txn.getReferenceId());
        response.setQuantity(txn.getQuantity());
        response.setConversionFactor(txn.getConversionFactor());
        response.setQuantityInPrimaryUOM(txn.getQuantityInPrimaryUOM());
        response.setRemarks(txn.getRemarks());
        response.setStatus(txn.getStatus());
        response.setCreatedAt(txn.getCreatedAt());
        response.setCreatedBy(txn.getCreatedBy());

        if (txn.getItem() != null) {
            response.setItemId(txn.getItem().getId());
            response.setItemNumber(txn.getItem().getItemNumber());
            response.setItemName(txn.getItem().getItemName());
        }
        if (txn.getWarehouse() != null) {
            response.setWarehouseId(txn.getWarehouse().getId());
            response.setWarehouseCode(txn.getWarehouse().getCode());
            response.setWarehouseName(txn.getWarehouse().getName());
        }
        if (txn.getUom() != null) {
            response.setUomId(txn.getUom().getId());
            response.setUomCode(txn.getUom().getCode());
        }

        return response;
    }
}
