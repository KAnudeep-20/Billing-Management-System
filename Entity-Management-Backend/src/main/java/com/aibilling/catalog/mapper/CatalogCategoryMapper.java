package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.CatalogCategoryRequest;
import com.aibilling.catalog.dto.CatalogCategoryResponse;
import com.aibilling.catalog.dto.CatalogCategoryTreeResponse;
import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.catalog.repository.CatalogCategoryRepository;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.common.enums.Status;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for CatalogCategory domain classes.
 */
@Component
public class CatalogCategoryMapper {

    private final CatalogItemRepository catalogItemRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;

    public CatalogCategoryMapper(
            CatalogItemRepository catalogItemRepository,
            CatalogCategoryRepository catalogCategoryRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.catalogCategoryRepository = catalogCategoryRepository;
    }

    public CatalogCategory toEntity(CatalogCategoryRequest request) {
        if (request == null) {
            return null;
        }

        CatalogCategory category = new CatalogCategory();
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return category;
    }

    public CatalogCategoryResponse toResponse(CatalogCategory category) {
        if (category == null) {
            return null;
        }

        CatalogCategoryResponse response = new CatalogCategoryResponse();

        response.setId(category.getId());
        response.setCode(category.getCode());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setStatus(category.getStatus());
        response.setCreatedAt(category.getCreatedAt());
        response.setCreatedBy(category.getCreatedBy());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setUpdatedBy(category.getUpdatedBy());

        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
            response.setParentCategoryName(category.getParentCategory().getName());
        }

        // Avoid LazyInitializationException
        response.setHasSubCategories(
                catalogCategoryRepository.existsByParentCategoryIdAndStatusNot(
                        category.getId(),
                        Status.DELETED));

        response.setHasItems(
                catalogItemRepository.existsByCategoryIdAndStatusNot(
                        category.getId(),
                        Status.DELETED));

        return response;
    }

    public CatalogCategoryTreeResponse toTreeResponse(CatalogCategory category) {
        if (category == null) {
            return null;
        }

        CatalogCategoryTreeResponse tree = new CatalogCategoryTreeResponse();

        tree.setId(category.getId());
        tree.setCode(category.getCode());
        tree.setName(category.getName());
        tree.setDescription(category.getDescription());
        tree.setStatus(category.getStatus());

        if (category.getSubCategories() != null) {
            List<CatalogCategoryTreeResponse> children = category.getSubCategories()
                    .stream()
                    .filter(sub -> sub.getStatus() != Status.DELETED)
                    .map(this::toTreeResponse)
                    .collect(Collectors.toList());

            tree.setChildren(children);
        }

        return tree;
    }

    public void updateEntityFromRequest(
            CatalogCategoryRequest request,
            CatalogCategory category) {

        if (request == null) {
            return;
        }

        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }
}
