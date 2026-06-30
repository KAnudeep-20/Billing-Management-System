package com.aibilling.ratecard.service.impl;

import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.ratecard.model.EntityRateCard;
import com.aibilling.ratecard.model.RateCard;
import com.aibilling.ratecard.model.RateCardItem;
import com.aibilling.ratecard.repository.EntityRateCardRepository;
import com.aibilling.ratecard.repository.RateCardItemRepository;
import com.aibilling.ratecard.repository.RateCardRepository;
import com.aibilling.ratecard.service.RateCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RateCardServiceImpl extends BaseServiceImpl<RateCard> implements RateCardService {

    private final RateCardRepository rateCardRepository;
    private final RateCardItemRepository rateCardItemRepository;
    private final EntityRateCardRepository entityRateCardRepository;
    private final CatalogItemRepository catalogItemRepository;

    public RateCardServiceImpl(RateCardRepository rateCardRepository,
                               RateCardItemRepository rateCardItemRepository,
                               EntityRateCardRepository entityRateCardRepository,
                               CatalogItemRepository catalogItemRepository) {
        super(rateCardRepository, "RateCard");
        this.rateCardRepository = rateCardRepository;
        this.rateCardItemRepository = rateCardItemRepository;
        this.entityRateCardRepository = entityRateCardRepository;
        this.catalogItemRepository = catalogItemRepository;
    }

    @Override
    @Transactional
    public RateCard create(RateCard entity) {
        validateUniqueness(null, entity.getRateCardCode(), entity.getRateCardName());
        validateEffectiveDates(entity.getEffectiveFrom(), entity.getEffectiveTo());
        return super.create(entity);
    }

    @Override
    @Transactional
    public RateCard update(UUID id, RateCard entity) {
        validateUniqueness(id, entity.getRateCardCode(), entity.getRateCardName());
        validateEffectiveDates(entity.getEffectiveFrom(), entity.getEffectiveTo());
        return super.update(id, entity);
    }

    @Override
    public Page<RateCard> search(String code, String name, Status status, Pageable pageable) {
        return rateCardRepository.searchRateCards(code, name, status, pageable);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? rateCardRepository.existsByRateCardCodeAndStatusNot(code, Status.DELETED)
                : rateCardRepository.existsByRateCardCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("RateCard", "rateCardCode", code);
        }

        boolean nameExists = (id == null)
                ? rateCardRepository.existsByRateCardNameAndStatusNot(name, Status.DELETED)
                : rateCardRepository.existsByRateCardNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("RateCard", "rateCardName", name);
        }
    }

    private void validateEffectiveDates(java.time.LocalDateTime from, java.time.LocalDateTime to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new BusinessException("Effective To date must be after Effective From date.");
        }
    }

    @Override
    public BigDecimal getPriceByRateCard(UUID rateCardId, UUID catalogItemId) {
        RateCard rc = findById(rateCardId);
        if (rc.getStatus() != Status.ACTIVE || !rc.isActiveFlag()) {
            throw new BusinessException("Rate card is not active.");
        }
        
        RateCardItem item = rateCardItemRepository.findActiveByRateCardAndCatalogItem(rateCardId, catalogItemId)
                .orElseThrow(() -> new ResourceNotFoundException("RateCardItem", "catalogItemId", catalogItemId.toString()));
        return item.getUnitPrice();
    }

    @Override
    public BigDecimal getPriceByCatalogItem(UUID catalogItemId) {
        CatalogItem ci = catalogItemRepository.findById(catalogItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CatalogItem", "id", catalogItemId.toString()));
        return ci.getListPrice();
    }

    @Override
    public BigDecimal getPriceByEntity(UUID entityId, UUID catalogItemId) {
        EntityRateCard erc = entityRateCardRepository.findActiveAssignment(entityId)
                .orElseThrow(() -> new BusinessException("No active rate card assigned to entity with id: " + entityId));
        return getPriceByRateCard(erc.getRateCard().getId(), catalogItemId);
    }
}
