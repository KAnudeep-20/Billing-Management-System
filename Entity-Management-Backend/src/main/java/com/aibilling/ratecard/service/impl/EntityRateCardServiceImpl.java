package com.aibilling.ratecard.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.ratecard.dto.EntityRateCardRequest;
import com.aibilling.ratecard.model.EntityRateCard;
import com.aibilling.ratecard.model.RateCard;
import com.aibilling.ratecard.repository.EntityRateCardRepository;
import com.aibilling.ratecard.repository.RateCardRepository;
import com.aibilling.ratecard.service.EntityRateCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EntityRateCardServiceImpl extends BaseServiceImpl<EntityRateCard> implements EntityRateCardService {

    private final EntityRateCardRepository entityRateCardRepository;
    private final EntityRepository entityRepository;
    private final RateCardRepository rateCardRepository;

    public EntityRateCardServiceImpl(EntityRateCardRepository entityRateCardRepository,
                                     EntityRepository entityRepository,
                                     RateCardRepository rateCardRepository) {
        super(entityRateCardRepository, "EntityRateCard");
        this.entityRateCardRepository = entityRateCardRepository;
        this.entityRepository = entityRepository;
        this.rateCardRepository = rateCardRepository;
    }

    @Override
    @Transactional
    public EntityRateCard assignRateCard(EntityRateCardRequest request) {
        log.info("Assigning rate card id={} to entity id={}", request.getRateCardId(), request.getEntityId());

        Entity entity = entityRepository.findById(request.getEntityId())
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", request.getEntityId().toString()));

        RateCard rc = rateCardRepository.findById(request.getRateCardId())
                .orElseThrow(() -> new ResourceNotFoundException("RateCard", "id", request.getRateCardId().toString()));

        if (rc.getStatus() != Status.ACTIVE || !rc.isActiveFlag()) {
            throw new BusinessException("Only active Rate Cards can be assigned to entities.");
        }

        if (request.getEffectiveFrom() != null && request.getEffectiveTo() != null && request.getEffectiveTo().isBefore(request.getEffectiveFrom())) {
            throw new BusinessException("Effective To date must be after Effective From date.");
        }

        // Deactivate previous active assignment
        Optional<EntityRateCard> activeOpt = entityRateCardRepository.findActiveAssignment(request.getEntityId());
        if (activeOpt.isPresent()) {
            EntityRateCard activeAss = activeOpt.get();
            activeAss.setActiveFlag(false);
            activeAss.setEffectiveTo(LocalDateTime.now());
            entityRateCardRepository.save(activeAss);
            log.info("Deactivated previous rate card assignment id={}", activeAss.getId());
        }

        EntityRateCard erc = new EntityRateCard();
        erc.setEntity(entity);
        erc.setRateCard(rc);
        erc.setEffectiveFrom(request.getEffectiveFrom());
        erc.setEffectiveTo(request.getEffectiveTo());
        erc.setActiveFlag(request.isActiveFlag());
        erc.setStatus(Status.ACTIVE);

        return entityRateCardRepository.save(erc);
    }

    @Override
    @Transactional
    public EntityRateCard replaceAssignment(UUID assignmentId, EntityRateCardRequest request) {
        log.info("Replacing rate card assignment id={} with rate card id={}", assignmentId, request.getRateCardId());

        EntityRateCard existing = entityRateCardRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("EntityRateCard", "id", assignmentId.toString()));

        if (!existing.getEntity().getId().equals(request.getEntityId())) {
            throw new BusinessException("Cannot replace assignment: entity ID mismatch.");
        }

        // Standard replace: deactivates existing and creates new one
        existing.setActiveFlag(false);
        existing.setEffectiveTo(LocalDateTime.now());
        entityRateCardRepository.save(existing);

        return assignRateCard(request);
    }

    @Override
    @Transactional
    public void removeAssignment(UUID assignmentId) {
        log.info("Removing rate card assignment id={}", assignmentId);

        EntityRateCard existing = entityRateCardRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("EntityRateCard", "id", assignmentId.toString()));

        existing.setStatus(Status.DELETED);
        existing.setActiveFlag(false);
        entityRateCardRepository.save(existing);
    }

    @Override
    public EntityRateCard getAssignedRateCard(UUID entityId) {
        return entityRateCardRepository.findActiveAssignment(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("EntityRateCard", "entityId", entityId.toString()));
    }

    @Override
    public Page<EntityRateCard> searchAssignments(String entityName, String rateCardName, Pageable pageable) {
        return entityRateCardRepository.searchAssignments(entityName, rateCardName, pageable);
    }
}
