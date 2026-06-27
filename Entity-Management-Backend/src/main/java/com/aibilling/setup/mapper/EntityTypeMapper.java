package com.aibilling.setup.mapper;

import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.EntityTypeCreateRequest;
import com.aibilling.setup.dto.EntityTypeResponse;
import com.aibilling.setup.dto.EntityTypeUpdateRequest;
import com.aibilling.setup.model.EntityType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for {@link EntityType} entities and DTOs.
 */
@Component
public class EntityTypeMapper {

    public EntityType toEntity(EntityTypeCreateRequest request) {
        if (request == null) return null;
        EntityType entity = new EntityType();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public EntityType toEntity(EntityTypeUpdateRequest request) {
        if (request == null) return null;
        EntityType entity = new EntityType();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        return entity;
    }

    public EntityTypeResponse toResponse(EntityType entity) {
        if (entity == null) return null;
        EntityTypeResponse response = new EntityTypeResponse();
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

    public LookupResponse toLookupResponse(EntityType entity) {
        if (entity == null) return null;
        LookupResponse response = new LookupResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        return response;
    }

    public List<LookupResponse> toLookupResponseList(List<EntityType> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toLookupResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(EntityTypeUpdateRequest request, EntityType entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }
}
