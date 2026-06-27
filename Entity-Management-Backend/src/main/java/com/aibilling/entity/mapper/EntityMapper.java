package com.aibilling.entity.mapper;

import com.aibilling.entity.dto.EntityCreateRequest;
import com.aibilling.entity.dto.EntityResponse;
import com.aibilling.entity.dto.EntityUpdateRequest;
import com.aibilling.entity.dto.OrganizationDetailsDto;
import com.aibilling.entity.dto.PersonDetailsDto;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.model.EntityDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for Entity domain classes.
 * Converts between DTOs and Entity/EntityDetails models.
 */
@Component
public class EntityMapper {

    public Entity toEntity(EntityCreateRequest request) {
        if (request == null) {
            return null;
        }
        Entity entity = new Entity();
        entity.setEntityCategory(request.getEntityCategory());

        EntityDetails details = new EntityDetails();
        details.setEntity(entity);
        if (request.getEntityCategory() == EntityCategory.ORGANIZATION && request.getOrganizationDetails() != null) {
            details.setOrganizationName(request.getOrganizationDetails().getOrganizationName());
            details.setTin(request.getOrganizationDetails().getTin());
        } else if (request.getEntityCategory() == EntityCategory.PERSON && request.getPersonDetails() != null) {
            details.setFullName(request.getPersonDetails().getFullName());
            details.setIdentificationType(request.getPersonDetails().getIdentificationType());
            details.setIdentificationNumber(request.getPersonDetails().getIdentificationNumber());
        }
        entity.setDetails(details);

        return entity;
    }

    public Entity toEntity(EntityUpdateRequest request) {
        if (request == null) {
            return null;
        }
        Entity entity = new Entity();
        entity.setEntityCategory(request.getEntityCategory());
        entity.setStatus(request.getStatus());

        EntityDetails details = new EntityDetails();
        details.setEntity(entity);
        if (request.getEntityCategory() == EntityCategory.ORGANIZATION && request.getOrganizationDetails() != null) {
            details.setOrganizationName(request.getOrganizationDetails().getOrganizationName());
            details.setTin(request.getOrganizationDetails().getTin());
        } else if (request.getEntityCategory() == EntityCategory.PERSON && request.getPersonDetails() != null) {
            details.setFullName(request.getPersonDetails().getFullName());
            details.setIdentificationType(request.getPersonDetails().getIdentificationType());
            details.setIdentificationNumber(request.getPersonDetails().getIdentificationNumber());
        }
        entity.setDetails(details);

        return entity;
    }

    public EntityResponse toResponse(Entity entity) {
        if (entity == null) {
            return null;
        }
        EntityResponse response = new EntityResponse();
        response.setId(entity.getId());
        response.setEntityCategory(entity.getEntityCategory());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUpdatedBy(entity.getUpdatedBy());

        if (entity.getDetails() != null) {
            if (entity.getEntityCategory() == EntityCategory.ORGANIZATION) {
                OrganizationDetailsDto orgDto = new OrganizationDetailsDto();
                orgDto.setOrganizationName(entity.getDetails().getOrganizationName());
                orgDto.setTin(entity.getDetails().getTin());
                response.setOrganizationDetails(orgDto);
            } else if (entity.getEntityCategory() == EntityCategory.PERSON) {
                PersonDetailsDto personDto = new PersonDetailsDto();
                personDto.setFullName(entity.getDetails().getFullName());
                personDto.setIdentificationType(entity.getDetails().getIdentificationType());
                personDto.setIdentificationNumber(entity.getDetails().getIdentificationNumber());
                response.setPersonDetails(personDto);
            }
        }

        if (entity.getEntityTypes() != null) {
            List<String> codes = entity.getEntityTypes().stream()
                    .filter(mapping -> mapping.getEntityType() != null)
                    .map(mapping -> mapping.getEntityType().getCode())
                    .collect(Collectors.toList());
            response.setEntityTypeCodes(codes);
        }

        return response;
    }

    public void updateEntityFromRequest(EntityUpdateRequest request, Entity entity) {
        if (request == null) {
            return;
        }
        entity.setEntityCategory(request.getEntityCategory());
        entity.setStatus(request.getStatus());

        EntityDetails details = entity.getDetails();
        if (details == null) {
            details = new EntityDetails();
            details.setEntity(entity);
            entity.setDetails(details);
        }

        if (request.getEntityCategory() == EntityCategory.ORGANIZATION) {
            details.setFullName(null);
            details.setIdentificationType(null);
            details.setIdentificationNumber(null);
            if (request.getOrganizationDetails() != null) {
                details.setOrganizationName(request.getOrganizationDetails().getOrganizationName());
                details.setTin(request.getOrganizationDetails().getTin());
            }
        } else if (request.getEntityCategory() == EntityCategory.PERSON) {
            details.setOrganizationName(null);
            details.setTin(null);
            if (request.getPersonDetails() != null) {
                details.setFullName(request.getPersonDetails().getFullName());
                details.setIdentificationType(request.getPersonDetails().getIdentificationType());
                details.setIdentificationNumber(request.getPersonDetails().getIdentificationNumber());
            }
        }
    }
}
