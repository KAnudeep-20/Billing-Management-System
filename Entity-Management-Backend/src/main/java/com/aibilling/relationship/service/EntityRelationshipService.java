package com.aibilling.relationship.service;

import com.aibilling.relationship.dto.EntityRelationshipCreateRequest;
import com.aibilling.relationship.model.EntityRelationship;

import java.util.List;
import java.util.UUID;

public interface EntityRelationshipService {

    EntityRelationship create(EntityRelationshipCreateRequest request);

    void delete(UUID id);

    List<EntityRelationship> listRelationships(UUID subjectId, UUID objectId, UUID entityId);
}
