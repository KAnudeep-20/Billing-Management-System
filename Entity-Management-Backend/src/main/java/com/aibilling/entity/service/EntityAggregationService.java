package com.aibilling.entity.service;

import com.aibilling.entity.dto.CompleteEntityDetailsResponse;

import java.util.UUID;

public interface EntityAggregationService {
    CompleteEntityDetailsResponse getCompleteEntityDetails(UUID entityId);
}
