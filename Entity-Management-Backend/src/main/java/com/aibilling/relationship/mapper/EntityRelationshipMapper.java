package com.aibilling.relationship.mapper;

import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.relationship.dto.EntityRelationshipResponse;
import com.aibilling.relationship.model.EntityRelationship;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityRelationshipMapper {

    public EntityRelationshipResponse toResponse(EntityRelationship relationship) {
        if (relationship == null) return null;

        EntityRelationshipResponse response = new EntityRelationshipResponse();
        response.setId(relationship.getId());
        response.setStatus(relationship.getStatus());
        response.setCreatedAt(relationship.getCreatedAt());
        response.setCreatedBy(relationship.getCreatedBy());
        response.setUpdatedAt(relationship.getUpdatedAt());
        response.setUpdatedBy(relationship.getUpdatedBy());

        // Subject Entity
        Entity subject = relationship.getSubjectEntity();
        if (subject != null) {
            response.setSubjectEntityId(subject.getId());
            response.setSubjectEntityCategory(subject.getEntityCategory());
            response.setSubjectEntityName(getEntityDisplayName(subject));
        }

        // Relationship Type
        if (relationship.getRelationshipType() != null) {
            response.setRelationshipTypeId(relationship.getRelationshipType().getId());
            response.setRelationshipTypeCode(relationship.getRelationshipType().getCode());
            response.setRelationshipTypeName(relationship.getRelationshipType().getName());
        }

        // Object Entity
        Entity object = relationship.getObjectEntity();
        if (object != null) {
            response.setObjectEntityId(object.getId());
            response.setObjectEntityCategory(object.getEntityCategory());
            response.setObjectEntityName(getEntityDisplayName(object));
        }

        return response;
    }

    public List<EntityRelationshipResponse> toResponseList(List<EntityRelationship> relationships) {
        if (relationships == null) return null;
        List<EntityRelationshipResponse> list = new ArrayList<>(relationships.size());
        for (EntityRelationship rel : relationships) {
            list.add(toResponse(rel));
        }
        return list;
    }

    private String getEntityDisplayName(Entity entity) {
        if (entity.getDetails() == null) {
            return "Unknown";
        }
        if (entity.getEntityCategory() == EntityCategory.PERSON) {
            return entity.getDetails().getFullName();
        } else {
            return entity.getDetails().getOrganizationName();
        }
    }
}
