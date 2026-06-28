package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.dto.CatalogCategoryTreeResponse;
import com.aibilling.catalog.mapper.CatalogCategoryMapper;
import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.catalog.repository.CatalogCategoryRepository;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.catalog.service.CatalogCategoryService;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Catalog Category entities.
 * Enforces leaf-category business rules and hierarchy validations.
 */
@Service
@Transactional(readOnly = true)
public class CatalogCategoryServiceImpl extends BaseServiceImpl<CatalogCategory> implements CatalogCategoryService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatalogCategoryServiceImpl.class);

    private final CatalogCategoryRepository categoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogCategoryMapper categoryMapper;

    public CatalogCategoryServiceImpl(CatalogCategoryRepository categoryRepository,
                                       CatalogItemRepository catalogItemRepository,
                                       CatalogCategoryMapper categoryMapper) {
        super(categoryRepository, "CatalogCategory");
        this.categoryRepository = categoryRepository;
        this.catalogItemRepository = catalogItemRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CatalogCategory create(CatalogCategory category) {
        return create(category, null);
    }

    @Override
    @Transactional
    public CatalogCategory create(CatalogCategory category, UUID parentCategoryId) {
        log.info("Creating catalog category code={}, parent={}", category.getCode(), parentCategoryId);

        validateUniqueCode(category.getCode(), null);
        validateUniqueName(category.getName(), null);

        if (parentCategoryId != null) {
            CatalogCategory parent = findById(parentCategoryId);
            // Parent must not have items assigned — only leaf categories can have items
            if (catalogItemRepository.existsByCategoryIdAndStatusNot(parentCategoryId, Status.DELETED)) {
                throw new BusinessException("Cannot add sub-category to category '" + parent.getName()
                        + "' because it already has catalog items assigned. Only leaf categories can hold items.");
            }
            category.setParentCategory(parent);
        }

        category.setStatus(Status.ACTIVE);
        CatalogCategory saved = categoryRepository.save(category);
        log.info("CatalogCategory created with id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }

    @Override
    @Transactional
    public CatalogCategory update(UUID id, CatalogCategory category) {
        return update(id, category, null);
    }

    @Override
    @Transactional
    public CatalogCategory update(UUID id, CatalogCategory category, UUID parentCategoryId) {
        log.info("Updating catalog category id={}, code={}, parent={}", id, category.getCode(), parentCategoryId);

        CatalogCategory existing = findById(id);
        validateUniqueCode(category.getCode(), id);
        validateUniqueName(category.getName(), id);

        if (parentCategoryId != null) {
            if (parentCategoryId.equals(id)) {
                throw new BusinessException("A category cannot be its own parent.");
            }
            CatalogCategory parent = findById(parentCategoryId);
            if (catalogItemRepository.existsByCategoryIdAndStatusNot(parentCategoryId, Status.DELETED)) {
                throw new BusinessException("Cannot set parent to category '" + parent.getName()
                        + "' because it already has catalog items assigned.");
            }
            existing.setParentCategory(parent);
        } else {
            existing.setParentCategory(null);
        }

        existing.setCode(category.getCode());
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());

        CatalogCategory saved = categoryRepository.save(existing);
        log.info("CatalogCategory updated with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        CatalogCategory category = findById(id);

        // Cannot delete if it has sub-categories
        if (categoryRepository.existsByParentCategoryIdAndStatusNot(id, Status.DELETED)) {
            throw new BusinessException("Cannot delete category '" + category.getName()
                    + "' because it has active sub-categories.");
        }

        // Cannot delete if it has items
        if (catalogItemRepository.existsByCategoryIdAndStatusNot(id, Status.DELETED)) {
            throw new BusinessException("Cannot delete category '" + category.getName()
                    + "' because it has catalog items assigned.");
        }

        category.setStatus(Status.DELETED);
        categoryRepository.save(category);
        log.info("CatalogCategory soft-deleted with id={}", id);
    }

    @Override
    public List<CatalogCategoryTreeResponse> getCategoryTree() {
        List<CatalogCategory> rootCategories = categoryRepository.findByStatus(Status.ACTIVE)
                .stream()
                .filter(c -> c.getParentCategory() == null)
                .collect(Collectors.toList());

        return rootCategories.stream()
                .map(categoryMapper::toTreeResponse)
                .collect(Collectors.toList());
    }

    // ==================== Private Helpers ====================

    private void validateUniqueCode(String code, UUID excludeId) {
        if (excludeId == null) {
            if (categoryRepository.existsByCodeAndStatusNot(code, Status.DELETED)) {
                throw new DuplicateResourceException("CatalogCategory", "code", code);
            }
        } else {
            if (categoryRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, excludeId)) {
                throw new DuplicateResourceException("CatalogCategory", "code", code);
            }
        }
    }

    private void validateUniqueName(String name, UUID excludeId) {
        if (excludeId == null) {
            if (categoryRepository.existsByNameAndStatusNot(name, Status.DELETED)) {
                throw new DuplicateResourceException("CatalogCategory", "name", name);
            }
        } else {
            if (categoryRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, excludeId)) {
                throw new DuplicateResourceException("CatalogCategory", "name", name);
            }
        }
    }
}
