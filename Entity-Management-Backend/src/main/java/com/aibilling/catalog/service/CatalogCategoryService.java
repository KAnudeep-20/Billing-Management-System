package com.aibilling.catalog.service;

import com.aibilling.catalog.dto.CatalogCategoryTreeResponse;
import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.common.service.BaseService;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing Catalog Category entities.
 */
public interface CatalogCategoryService extends BaseService<CatalogCategory> {

    /**
     * Creates a new category with optional parent assignment.
     */
    CatalogCategory create(CatalogCategory category, UUID parentCategoryId);

    /**
     * Updates an existing category with optional parent re-assignment.
     */
    CatalogCategory update(UUID id, CatalogCategory category, UUID parentCategoryId);

    /**
     * Retrieves the full category hierarchy as a tree.
     */
    List<CatalogCategoryTreeResponse> getCategoryTree();
}
