package com.aibilling.setup.mapper;

import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.RelationshipTypeCreateRequest;
import com.aibilling.setup.dto.RelationshipTypeResponse;
import com.aibilling.setup.dto.RelationshipTypeUpdateRequest;
import com.aibilling.setup.model.RelationshipType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for {@link RelationshipType} entities and DTOs.
 */
@Component
public class RelationshipTypeMapper {

    public RelationshipType toEntity(RelationshipTypeCreateRequest request) {
        if (request == null) return null;
        RelationshipType entity = new RelationshipType();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public RelationshipType toEntity(RelationshipTypeUpdateRequest request) {
        if (request == null) return null;
        RelationshipType entity = new RelationshipType();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        return entity;
    }

    public RelationshipTypeResponse toResponse(RelationshipType entity) {
        if (entity == null) return null;
        RelationshipTypeResponse response = new RelationshipTypeResponse();
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

    public LookupResponse toLookupResponse(RelationshipType entity) {
        if (entity == null) return null;
        LookupResponse response = new LookupResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        return response;
    }

    public List<LookupResponse> toLookupResponseList(List<RelationshipType> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toLookupResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(RelationshipTypeUpdateRequest request, RelationshipType entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }
}
