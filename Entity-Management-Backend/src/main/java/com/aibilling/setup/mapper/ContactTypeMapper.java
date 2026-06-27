package com.aibilling.setup.mapper;

import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.setup.dto.ContactTypeCreateRequest;
import com.aibilling.setup.dto.ContactTypeResponse;
import com.aibilling.setup.dto.ContactTypeUpdateRequest;
import com.aibilling.setup.model.ContactType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for {@link ContactType} entities and DTOs.
 */
@Component
public class ContactTypeMapper {

    public ContactType toEntity(ContactTypeCreateRequest request) {
        if (request == null) return null;
        ContactType entity = new ContactType();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public ContactType toEntity(ContactTypeUpdateRequest request) {
        if (request == null) return null;
        ContactType entity = new ContactType();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        return entity;
    }

    public ContactTypeResponse toResponse(ContactType entity) {
        if (entity == null) return null;
        ContactTypeResponse response = new ContactTypeResponse();
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

    public LookupResponse toLookupResponse(ContactType entity) {
        if (entity == null) return null;
        LookupResponse response = new LookupResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        return response;
    }

    public List<LookupResponse> toLookupResponseList(List<ContactType> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toLookupResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(ContactTypeUpdateRequest request, ContactType entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
    }
}
