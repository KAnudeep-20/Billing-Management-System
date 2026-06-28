package com.aibilling.catalog.service;

import com.aibilling.catalog.dto.ItemUomRequest;
import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.model.ItemUom;
import com.aibilling.common.service.BaseService;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing Catalog Item entities.
 */
public interface CatalogItemService extends BaseService<CatalogItem> {

    /**
     * Creates a new catalog item with category and UOM assignment.
     */
    CatalogItem create(CatalogItem item, UUID categoryId, UUID primaryUomId);

    /**
     * Updates an existing catalog item with category and UOM re-assignment.
     */
    CatalogItem update(UUID id, CatalogItem item, UUID categoryId, UUID primaryUomId);

    /**
     * Adds a secondary UOM mapping to a catalog item.
     */
    ItemUom addItemUom(UUID itemId, ItemUomRequest request);

    /**
     * Updates a secondary UOM mapping.
     */
    ItemUom updateItemUom(UUID itemId, UUID itemUomId, ItemUomRequest request);

    /**
     * Removes a secondary UOM mapping (soft delete).
     */
    void removeItemUom(UUID itemId, UUID itemUomId);

    /**
     * Retrieves all secondary UOM mappings for a catalog item.
     */
    List<ItemUom> getItemUoms(UUID itemId);
}
