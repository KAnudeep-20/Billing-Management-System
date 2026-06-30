package com.aibilling.ratecard.mapper;

import com.aibilling.ratecard.dto.EntityRateCardResponse;
import com.aibilling.ratecard.model.EntityRateCard;
import org.springframework.stereotype.Component;

@Component
public class EntityRateCardMapper {

    public EntityRateCardResponse toResponse(EntityRateCard erc) {
        if (erc == null) return null;
        EntityRateCardResponse resp = new EntityRateCardResponse();
        resp.setId(erc.getId());
        resp.setEntityId(erc.getEntity().getId());
        
        String entityName = "";
        if (erc.getEntity().getDetails() != null) {
            entityName = erc.getEntity().getDetails().getOrganizationName();
            if (entityName == null || entityName.trim().isEmpty()) {
                entityName = erc.getEntity().getDetails().getFullName();
            }
        }
        resp.setEntityName(entityName);
        
        resp.setRateCardId(erc.getRateCard().getId());
        resp.setRateCardName(erc.getRateCard().getRateCardName());
        resp.setRateCardCode(erc.getRateCard().getRateCardCode());
        resp.setEffectiveFrom(erc.getEffectiveFrom());
        resp.setEffectiveTo(erc.getEffectiveTo());
        resp.setActiveFlag(erc.isActiveFlag());
        resp.setStatus(erc.getStatus());
        resp.setCreatedAt(erc.getCreatedAt());
        resp.setCreatedBy(erc.getCreatedBy());
        resp.setUpdatedAt(erc.getUpdatedAt());
        resp.setUpdatedBy(erc.getUpdatedBy());
        return resp;
    }
}
