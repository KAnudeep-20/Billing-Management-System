package com.aibilling.setup.mapper;

import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.PaymentTermCreateRequest;
import com.aibilling.setup.dto.PaymentTermResponse;
import com.aibilling.setup.dto.PaymentTermUpdateRequest;
import com.aibilling.setup.model.PaymentTerm;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for {@link PaymentTerm} entities and DTOs.
 */
@Component
public class PaymentTermMapper {

    public PaymentTerm toEntity(PaymentTermCreateRequest request) {
        if (request == null) return null;
        PaymentTerm entity = new PaymentTerm();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public PaymentTerm toEntity(PaymentTermUpdateRequest request) {
        if (request == null) return null;
        PaymentTerm entity = new PaymentTerm();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        return entity;
    }

    public PaymentTermResponse toResponse(PaymentTerm entity) {
        if (entity == null) return null;
        PaymentTermResponse response = new PaymentTermResponse();
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

    public LookupResponse toLookupResponse(PaymentTerm entity) {
        if (entity == null) return null;
        LookupResponse response = new LookupResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        return response;
    }

    public List<LookupResponse> toLookupResponseList(List<PaymentTerm> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toLookupResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(PaymentTermUpdateRequest request, PaymentTerm entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }
}
