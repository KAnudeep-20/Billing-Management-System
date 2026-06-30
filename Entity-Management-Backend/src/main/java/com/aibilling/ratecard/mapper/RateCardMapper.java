package com.aibilling.ratecard.mapper;

import com.aibilling.ratecard.dto.RateCardCreateRequest;
import com.aibilling.ratecard.dto.RateCardResponse;
import com.aibilling.ratecard.dto.RateCardUpdateRequest;
import com.aibilling.ratecard.model.RateCard;
import org.springframework.stereotype.Component;

@Component
public class RateCardMapper {

    public RateCard toEntity(RateCardCreateRequest request) {
        if (request == null) return null;
        RateCard rc = new RateCard();
        rc.setRateCardCode(request.getRateCardCode());
        rc.setRateCardName(request.getRateCardName());
        rc.setDescription(request.getDescription());
        rc.setCurrency(request.getCurrency());
        rc.setEffectiveFrom(request.getEffectiveFrom());
        rc.setEffectiveTo(request.getEffectiveTo());
        rc.setActiveFlag(request.isActiveFlag());
        return rc;
    }

    public RateCard toEntity(RateCardUpdateRequest request) {
        if (request == null) return null;
        RateCard rc = new RateCard();
        rc.setRateCardCode(request.getRateCardCode());
        rc.setRateCardName(request.getRateCardName());
        rc.setDescription(request.getDescription());
        rc.setCurrency(request.getCurrency());
        rc.setEffectiveFrom(request.getEffectiveFrom());
        rc.setEffectiveTo(request.getEffectiveTo());
        rc.setActiveFlag(request.isActiveFlag());
        return rc;
    }

    public RateCardResponse toResponse(RateCard rc) {
        if (rc == null) return null;
        RateCardResponse resp = new RateCardResponse();
        resp.setId(rc.getId());
        resp.setRateCardCode(rc.getRateCardCode());
        resp.setRateCardName(rc.getRateCardName());
        resp.setDescription(rc.getDescription());
        resp.setCurrency(rc.getCurrency());
        resp.setStatus(rc.getStatus());
        resp.setEffectiveFrom(rc.getEffectiveFrom());
        resp.setEffectiveTo(rc.getEffectiveTo());
        resp.setActiveFlag(rc.isActiveFlag());
        resp.setCreatedAt(rc.getCreatedAt());
        resp.setCreatedBy(rc.getCreatedBy());
        resp.setUpdatedAt(rc.getUpdatedAt());
        resp.setUpdatedBy(rc.getUpdatedBy());
        return resp;
    }

    public void updateEntityFromRequest(RateCardUpdateRequest request, RateCard rc) {
        if (request == null) return;
        rc.setRateCardCode(request.getRateCardCode());
        rc.setRateCardName(request.getRateCardName());
        rc.setDescription(request.getDescription());
        rc.setCurrency(request.getCurrency());
        rc.setEffectiveFrom(request.getEffectiveFrom());
        rc.setEffectiveTo(request.getEffectiveTo());
        rc.setActiveFlag(request.isActiveFlag());
    }
}
