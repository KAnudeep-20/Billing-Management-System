package com.aibilling.relationship.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class EntityRelationshipCreateRequest {

    @NotNull(message = "Subject Entity ID is required")
    private UUID subjectEntityId;

    @NotNull(message = "Relationship Type ID is required")
    private UUID relationshipTypeId;

    @NotNull(message = "Object Entity ID is required")
    private UUID objectEntityId;

    public UUID getSubjectEntityId() {
        return subjectEntityId;
    }

    public void setSubjectEntityId(UUID subjectEntityId) {
        this.subjectEntityId = subjectEntityId;
    }

    public UUID getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(UUID relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }

    public UUID getObjectEntityId() {
        return objectEntityId;
    }

    public void setObjectEntityId(UUID objectEntityId) {
        this.objectEntityId = objectEntityId;
    }
}
