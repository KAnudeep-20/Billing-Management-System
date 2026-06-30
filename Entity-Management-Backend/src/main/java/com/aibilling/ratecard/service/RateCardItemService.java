package com.aibilling.ratecard.service;

import com.aibilling.common.service.BaseService;
import com.aibilling.ratecard.dto.RateCardItemRequest;
import com.aibilling.ratecard.dto.RateCardItemUpdateRequest;
import com.aibilling.ratecard.model.RateCardItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RateCardItemService extends BaseService<RateCardItem> {
    RateCardItem addItem(UUID rateCardId, RateCardItemRequest request);
    RateCardItem updatePrice(UUID rateCardId, UUID itemId, RateCardItemUpdateRequest request);
    void removeItem(UUID rateCardId, UUID itemId);
    List<RateCardItem> getItems(UUID rateCardId);
    Page<RateCardItem> searchItems(UUID rateCardId, String itemCode, String itemName, Pageable pageable);
}
