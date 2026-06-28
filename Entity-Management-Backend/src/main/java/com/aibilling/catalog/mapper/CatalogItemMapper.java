package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.CatalogItemRequest;
import com.aibilling.catalog.dto.CatalogItemResponse;
import com.aibilling.catalog.model.CatalogItem;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for CatalogItem domain classes.
 */
@Component
public class CatalogItemMapper {

    public CatalogItem toEntity(CatalogItemRequest request) {
        if (request == null) return null;
        CatalogItem item = new CatalogItem();
        item.setItemNumber(request.getItemNumber());
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setListPrice(request.getListPrice());
        item.setIsStocked(request.getIsStocked());
        item.setIsInventoryTracked(request.getIsInventoryTracked());
        item.setIsService(request.getIsService());
        item.setIsSellable(request.getIsSellable());
        item.setIsPurchasable(request.getIsPurchasable());
        return item;
    }

    public CatalogItemResponse toResponse(CatalogItem item) {
        if (item == null) return null;
        CatalogItemResponse response = new CatalogItemResponse();
        response.setId(item.getId());
        response.setItemNumber(item.getItemNumber());
        response.setItemName(item.getItemName());
        response.setDescription(item.getDescription());
        response.setListPrice(item.getListPrice());
        response.setIsStocked(item.getIsStocked());
        response.setIsInventoryTracked(item.getIsInventoryTracked());
        response.setIsService(item.getIsService());
        response.setIsSellable(item.getIsSellable());
        response.setIsPurchasable(item.getIsPurchasable());
        response.setStatus(item.getStatus());
        response.setCreatedAt(item.getCreatedAt());
        response.setCreatedBy(item.getCreatedBy());
        response.setUpdatedAt(item.getUpdatedAt());
        response.setUpdatedBy(item.getUpdatedBy());

        if (item.getCategory() != null) {
            response.setCategoryId(item.getCategory().getId());
            response.setCategoryName(item.getCategory().getName());
        }
        if (item.getPrimaryUom() != null) {
            response.setPrimaryUomId(item.getPrimaryUom().getId());
            response.setPrimaryUomCode(item.getPrimaryUom().getCode());
        }

        return response;
    }

    public void updateEntityFromRequest(CatalogItemRequest request, CatalogItem item) {
        if (request == null) return;
        item.setItemNumber(request.getItemNumber());
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setListPrice(request.getListPrice());
        item.setIsStocked(request.getIsStocked());
        item.setIsInventoryTracked(request.getIsInventoryTracked());
        item.setIsService(request.getIsService());
        item.setIsSellable(request.getIsSellable());
        item.setIsPurchasable(request.getIsPurchasable());
    }
}
