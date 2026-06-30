package com.aibilling.ratecard.service.impl;

import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.ratecard.dto.RateCardItemRequest;
import com.aibilling.ratecard.dto.RateCardItemUpdateRequest;
import com.aibilling.ratecard.model.RateCard;
import com.aibilling.ratecard.model.RateCardItem;
import com.aibilling.ratecard.repository.RateCardItemRepository;
import com.aibilling.ratecard.repository.RateCardRepository;
import com.aibilling.ratecard.service.RateCardItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RateCardItemServiceImpl extends BaseServiceImpl<RateCardItem> implements RateCardItemService {

    private final RateCardItemRepository rateCardItemRepository;
    private final RateCardRepository rateCardRepository;
    private final CatalogItemRepository catalogItemRepository;

    public RateCardItemServiceImpl(RateCardItemRepository rateCardItemRepository,
                                   RateCardRepository rateCardRepository,
                                   CatalogItemRepository catalogItemRepository) {
        super(rateCardItemRepository, "RateCardItem");
        this.rateCardItemRepository = rateCardItemRepository;
        this.rateCardRepository = rateCardRepository;
        this.catalogItemRepository = catalogItemRepository;
    }

    @Override
    @Transactional
    public RateCardItem addItem(UUID rateCardId, RateCardItemRequest request) {
        log.info("Adding item to rate card id={}, catalogItemId={}", rateCardId, request.getCatalogItemId());

        RateCard rc = rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCard", "id", rateCardId.toString()));

        CatalogItem ci = catalogItemRepository.findById(request.getCatalogItemId())
                .orElseThrow(() -> new ResourceNotFoundException("CatalogItem", "id", request.getCatalogItemId().toString()));

        // Business rules check
        if (ci.getStatus() != Status.ACTIVE) {
            throw new BusinessException("Catalog Item must be active.");
        }
        if (!ci.getIsSellable()) {
            throw new BusinessException("Catalog Item must be sellable.");
        }
        if (request.getUnitPrice() == null || request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Unit price must be greater than zero.");
        }

        // Duplicate check
        boolean exists = rateCardItemRepository.existsByRateCardIdAndCatalogItemIdAndStatusNot(rateCardId, request.getCatalogItemId(), Status.DELETED);
        if (exists) {
            throw new DuplicateResourceException("RateCardItem", "catalogItemId", request.getCatalogItemId().toString());
        }

        RateCardItem item = new RateCardItem();
        item.setRateCard(rc);
        item.setCatalogItem(ci);
        item.setPrimaryUom(ci.getPrimaryUom()); // automatically copied
        item.setUnitPrice(request.getUnitPrice());
        item.setRemarks(request.getRemarks());
        item.setStatus(Status.ACTIVE);

        return rateCardItemRepository.save(item);
    }

    @Override
    @Transactional
    public RateCardItem updatePrice(UUID rateCardId, UUID itemId, RateCardItemUpdateRequest request) {
        log.info("Updating price for rate card item id={} under rate card id={}", itemId, rateCardId);

        RateCardItem item = rateCardItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCardItem", "id", itemId.toString()));

        if (!item.getRateCard().getId().equals(rateCardId)) {
            throw new BusinessException("Rate card item does not belong to the specified rate card.");
        }

        if (request.getUnitPrice() == null || request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Unit price must be greater than zero.");
        }

        item.setUnitPrice(request.getUnitPrice());
        item.setRemarks(request.getRemarks());

        return rateCardItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(UUID rateCardId, UUID itemId) {
        log.info("Removing item id={} from rate card id={}", itemId, rateCardId);

        RateCardItem item = rateCardItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCardItem", "id", itemId.toString()));

        if (!item.getRateCard().getId().equals(rateCardId)) {
            throw new BusinessException("Rate card item does not belong to the specified rate card.");
        }

        item.setStatus(Status.DELETED);
        rateCardItemRepository.save(item);
    }

    @Override
    public List<RateCardItem> getItems(UUID rateCardId) {
        // Verify rate card exists
        rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCard", "id", rateCardId.toString()));
        return rateCardItemRepository.findByRateCardIdAndStatus(rateCardId, Status.ACTIVE);
    }

    @Override
    public Page<RateCardItem> searchItems(UUID rateCardId, String itemCode, String itemName, Pageable pageable) {
        // Verify rate card exists
        rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCard", "id", rateCardId.toString()));
        return rateCardItemRepository.searchItems(rateCardId, itemCode, itemName, pageable);
    }
}
