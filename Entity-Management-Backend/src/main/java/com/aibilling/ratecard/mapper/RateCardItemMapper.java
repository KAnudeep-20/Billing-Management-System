package com.aibilling.ratecard.mapper;

import com.aibilling.ratecard.dto.RateCardItemResponse;
import com.aibilling.ratecard.model.RateCardItem;
import org.springframework.stereotype.Component;

@Component
public class RateCardItemMapper {

    public RateCardItemResponse toResponse(RateCardItem item) {
        if (item == null) return null;
        RateCardItemResponse resp = new RateCardItemResponse();
        resp.setId(item.getId());
        resp.setRateCardId(item.getRateCard().getId());
        resp.setCatalogItemId(item.getCatalogItem().getId());
        resp.setCatalogItemNumber(item.getCatalogItem().getItemNumber());
        resp.setCatalogItemName(item.getCatalogItem().getItemName());
        resp.setPrimaryUomId(item.getPrimaryUom().getId());
        resp.setPrimaryUomCode(item.getPrimaryUom().getCode());
        resp.setUnitPrice(item.getUnitPrice());
        resp.setRemarks(item.getRemarks());
        resp.setStatus(item.getStatus());
        resp.setCreatedAt(item.getCreatedAt());
        resp.setCreatedBy(item.getCreatedBy());
        resp.setUpdatedAt(item.getUpdatedAt());
        resp.setUpdatedBy(item.getUpdatedBy());
        return resp;
    }
}
