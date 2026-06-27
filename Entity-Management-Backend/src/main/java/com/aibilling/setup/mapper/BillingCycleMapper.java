package com.aibilling.setup.mapper;

import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.BillingCycleCreateRequest;
import com.aibilling.setup.dto.BillingCycleResponse;
import com.aibilling.setup.dto.BillingCycleUpdateRequest;
import com.aibilling.setup.model.BillingCycle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for {@link BillingCycle} entities and DTOs.
 */
@Component
public class BillingCycleMapper {

    public BillingCycle toEntity(BillingCycleCreateRequest request) {
        if (request == null) return null;
        BillingCycle entity = new BillingCycle();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public BillingCycle toEntity(BillingCycleUpdateRequest request) {
        if (request == null) return null;
        BillingCycle entity = new BillingCycle();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        return entity;
    }

    public BillingCycleResponse toResponse(BillingCycle entity) {
        if (entity == null) return null;
        BillingCycleResponse response = new BillingCycleResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }

    public LookupResponse toLookupResponse(BillingCycle entity) {
        if (entity == null) return null;
        LookupResponse response = new LookupResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        return response;
    }

    public List<LookupResponse> toLookupResponseList(List<BillingCycle> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toLookupResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(BillingCycleUpdateRequest request, BillingCycle entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }
}
