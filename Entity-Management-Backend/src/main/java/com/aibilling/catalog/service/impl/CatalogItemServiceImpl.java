package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.dto.ItemUomRequest;
import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.model.ItemUom;
import com.aibilling.catalog.model.Uom;
import com.aibilling.catalog.repository.CatalogCategoryRepository;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.catalog.repository.ItemUomRepository;
import com.aibilling.catalog.repository.UomRepository;
import com.aibilling.catalog.service.CatalogItemService;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing Catalog Item entities.
 * Enforces leaf-category assignment, item behavior flags, and UOM mapping rules.
 */
@Service
@Transactional(readOnly = true)
public class CatalogItemServiceImpl extends BaseServiceImpl<CatalogItem> implements CatalogItemService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatalogItemServiceImpl.class);

    private final CatalogItemRepository itemRepository;
    private final CatalogCategoryRepository categoryRepository;
    private final UomRepository uomRepository;
    private final ItemUomRepository itemUomRepository;

    public CatalogItemServiceImpl(CatalogItemRepository itemRepository,
                                   CatalogCategoryRepository categoryRepository,
                                   UomRepository uomRepository,
                                   ItemUomRepository itemUomRepository) {
        super(itemRepository, "CatalogItem");
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.uomRepository = uomRepository;
        this.itemUomRepository = itemUomRepository;
    }

    @Override
    @Transactional
    public CatalogItem create(CatalogItem item) {
        throw new UnsupportedOperationException("Use create(item, categoryId, primaryUomId) instead.");
    }

    @Override
    @Transactional
    public CatalogItem update(UUID id, CatalogItem item) {
        throw new UnsupportedOperationException("Use update(id, item, categoryId, primaryUomId) instead.");
    }

    @Override
    @Transactional
    public CatalogItem create(CatalogItem item, UUID categoryId, UUID primaryUomId) {
        log.info("Creating catalog item number={}, category={}", item.getItemNumber(), categoryId);

        validateUniqueItemNumber(item.getItemNumber(), null);
        validateItemBehaviorFlags(item);

        CatalogCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CatalogCategory", "id", categoryId.toString()));
        validateLeafCategory(category);

        Uom primaryUom = uomRepository.findById(primaryUomId)
                .orElseThrow(() -> new ResourceNotFoundException("UOM", "id", primaryUomId.toString()));

        item.setCategory(category);
        item.setPrimaryUom(primaryUom);
        item.setStatus(Status.ACTIVE);

        CatalogItem saved = itemRepository.save(item);
        log.info("CatalogItem created with id={}, itemNumber={}", saved.getId(), saved.getItemNumber());
        return saved;
    }

    @Override
    @Transactional
    public CatalogItem update(UUID id, CatalogItem item, UUID categoryId, UUID primaryUomId) {
        log.info("Updating catalog item id={}, number={}", id, item.getItemNumber());

        CatalogItem existing = findById(id);
        validateUniqueItemNumber(item.getItemNumber(), id);
        validateItemBehaviorFlags(item);

        CatalogCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CatalogCategory", "id", categoryId.toString()));
        validateLeafCategory(category);

        Uom primaryUom = uomRepository.findById(primaryUomId)
                .orElseThrow(() -> new ResourceNotFoundException("UOM", "id", primaryUomId.toString()));

        existing.setItemNumber(item.getItemNumber());
        existing.setItemName(item.getItemName());
        existing.setDescription(item.getDescription());
        existing.setListPrice(item.getListPrice());
        existing.setIsStocked(item.getIsStocked());
        existing.setIsInventoryTracked(item.getIsInventoryTracked());
        existing.setIsService(item.getIsService());
        existing.setIsSellable(item.getIsSellable());
        existing.setIsPurchasable(item.getIsPurchasable());
        existing.setCategory(category);
        existing.setPrimaryUom(primaryUom);

        CatalogItem saved = itemRepository.save(existing);
        log.info("CatalogItem updated with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public ItemUom addItemUom(UUID itemId, ItemUomRequest request) {
        log.info("Adding UOM mapping to item id={}, uomId={}", itemId, request.getUomId());

        CatalogItem item = findById(itemId);
        Uom uom = uomRepository.findById(request.getUomId())
                .orElseThrow(() -> new ResourceNotFoundException("UOM", "id", request.getUomId().toString()));

        if (itemUomRepository.existsByItemIdAndUomIdAndStatusNot(itemId, request.getUomId(), Status.DELETED)) {
            throw new DuplicateResourceException("ItemUom", "uomId", request.getUomId().toString());
        }

        if (request.getConversionFactor() == null || request.getConversionFactor().signum() <= 0) {
            throw new BusinessException("Conversion factor must be greater than zero.");
        }

        ItemUom itemUom = new ItemUom();
        itemUom.setItem(item);
        itemUom.setUom(uom);
        itemUom.setConversionFactor(request.getConversionFactor());
        itemUom.setIsDefault(request.getIsDefault());
        itemUom.setStatus(Status.ACTIVE);

        ItemUom saved = itemUomRepository.save(itemUom);
        log.info("ItemUom created with id={} for item={}", saved.getId(), itemId);
        return saved;
    }

    @Override
    @Transactional
    public ItemUom updateItemUom(UUID itemId, UUID itemUomId, ItemUomRequest request) {
        log.info("Updating UOM mapping id={} for item id={}", itemUomId, itemId);

        findById(itemId); // validate item exists
        ItemUom existing = itemUomRepository.findById(itemUomId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemUom", "id", itemUomId.toString()));

        if (!existing.getItem().getId().equals(itemId)) {
            throw new BusinessException("ItemUom does not belong to the specified item.");
        }

        Uom uom = uomRepository.findById(request.getUomId())
                .orElseThrow(() -> new ResourceNotFoundException("UOM", "id", request.getUomId().toString()));

        if (itemUomRepository.existsByItemIdAndUomIdAndStatusNotAndIdNot(
                itemId, request.getUomId(), Status.DELETED, itemUomId)) {
            throw new DuplicateResourceException("ItemUom", "uomId", request.getUomId().toString());
        }

        if (request.getConversionFactor() == null || request.getConversionFactor().signum() <= 0) {
            throw new BusinessException("Conversion factor must be greater than zero.");
        }

        existing.setUom(uom);
        existing.setConversionFactor(request.getConversionFactor());
        existing.setIsDefault(request.getIsDefault());

        ItemUom saved = itemUomRepository.save(existing);
        log.info("ItemUom updated with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void removeItemUom(UUID itemId, UUID itemUomId) {
        log.info("Removing UOM mapping id={} from item id={}", itemUomId, itemId);

        ItemUom existing = itemUomRepository.findById(itemUomId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemUom", "id", itemUomId.toString()));

        if (!existing.getItem().getId().equals(itemId)) {
            throw new BusinessException("ItemUom does not belong to the specified item.");
        }

        existing.setStatus(Status.DELETED);
        itemUomRepository.save(existing);
        log.info("ItemUom soft-deleted with id={}", itemUomId);
    }

    @Override
    public List<ItemUom> getItemUoms(UUID itemId) {
        findById(itemId); // validate item exists
        return itemUomRepository.findByItemIdAndStatus(itemId, Status.ACTIVE);
    }

    // ==================== Private Helpers ====================

    private void validateUniqueItemNumber(String itemNumber, UUID excludeId) {
        if (excludeId == null) {
            if (itemRepository.existsByItemNumberAndStatusNot(itemNumber, Status.DELETED)) {
                throw new DuplicateResourceException("CatalogItem", "itemNumber", itemNumber);
            }
        } else {
            if (itemRepository.existsByItemNumberAndStatusNotAndIdNot(itemNumber, Status.DELETED, excludeId)) {
                throw new DuplicateResourceException("CatalogItem", "itemNumber", itemNumber);
            }
        }
    }

    /**
     * Validates that a category is a leaf (has no sub-categories).
     */
    private void validateLeafCategory(CatalogCategory category) {
        if (categoryRepository.existsByParentCategoryIdAndStatusNot(category.getId(), Status.DELETED)) {
            throw new BusinessException("Items can only be assigned to leaf categories. Category '"
                    + category.getName() + "' has sub-categories.");
        }
    }

    /**
     * Validates item behavior flag consistency.
     */
    private void validateItemBehaviorFlags(CatalogItem item) {
        if (item.getIsInventoryTracked() && !item.getIsStocked()) {
            throw new BusinessException("An inventory-tracked item must also be stocked.");
        }
        if (item.getIsStocked() && item.getIsService()) {
            throw new BusinessException("A stocked item cannot be a service item.");
        }
    }
}
