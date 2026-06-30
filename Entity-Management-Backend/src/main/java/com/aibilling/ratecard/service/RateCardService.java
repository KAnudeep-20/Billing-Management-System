package com.aibilling.ratecard.service;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.BaseService;
import com.aibilling.ratecard.model.RateCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface RateCardService extends BaseService<RateCard> {
    Page<RateCard> search(String code, String name, Status status, Pageable pageable);
    
    // Future readiness price lookup methods
    BigDecimal getPriceByRateCard(UUID rateCardId, UUID catalogItemId);
    BigDecimal getPriceByCatalogItem(UUID catalogItemId);
    BigDecimal getPriceByEntity(UUID entityId, UUID catalogItemId);
}
