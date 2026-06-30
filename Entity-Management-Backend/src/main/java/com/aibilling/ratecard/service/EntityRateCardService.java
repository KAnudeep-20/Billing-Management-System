package com.aibilling.ratecard.service;

import com.aibilling.common.service.BaseService;
import com.aibilling.ratecard.dto.EntityRateCardRequest;
import com.aibilling.ratecard.model.EntityRateCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EntityRateCardService extends BaseService<EntityRateCard> {
    EntityRateCard assignRateCard(EntityRateCardRequest request);
    EntityRateCard replaceAssignment(UUID assignmentId, EntityRateCardRequest request);
    void removeAssignment(UUID assignmentId);
    EntityRateCard getAssignedRateCard(UUID entityId);
    Page<EntityRateCard> searchAssignments(String entityName, String rateCardName, Pageable pageable);
}
