package com.aibilling.relationship.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.relationship.dto.EntityRelationshipCreateRequest;
import com.aibilling.relationship.model.EntityRelationship;
import com.aibilling.relationship.repository.EntityRelationshipRepository;
import com.aibilling.relationship.service.EntityRelationshipService;
import com.aibilling.setup.model.RelationshipType;
import com.aibilling.setup.repository.RelationshipTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EntityRelationshipServiceImpl implements EntityRelationshipService {

    private final EntityRelationshipRepository entityRelationshipRepository;
    private final EntityRepository entityRepository;
    private final RelationshipTypeRepository relationshipTypeRepository;

    public EntityRelationshipServiceImpl(EntityRelationshipRepository entityRelationshipRepository,
                                         EntityRepository entityRepository,
                                         RelationshipTypeRepository relationshipTypeRepository) {
        this.entityRelationshipRepository = entityRelationshipRepository;
        this.entityRepository = entityRepository;
        this.relationshipTypeRepository = relationshipTypeRepository;
    }

    @Override
    @Transactional
    public EntityRelationship create(EntityRelationshipCreateRequest request) {
        // Prevent self-referencing relationships
        if (request.getSubjectEntityId().equals(request.getObjectEntityId())) {
            throw new BusinessException("Self-referencing relationships are not allowed.");
        }

        // Fetch and validate Subject Entity
        Entity subject = entityRepository.findById(request.getSubjectEntityId())
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", request.getSubjectEntityId().toString()));
        if (subject.getStatus() == Status.DELETED) {
            throw new BusinessException("Subject entity is deleted");
        }

        // Fetch and validate Object Entity
        Entity object = entityRepository.findById(request.getObjectEntityId())
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", request.getObjectEntityId().toString()));
        if (object.getStatus() == Status.DELETED) {
            throw new BusinessException("Object entity is deleted");
        }

        // Fetch and validate Relationship Type
        RelationshipType type = relationshipTypeRepository.findById(request.getRelationshipTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RelationshipType", "id", request.getRelationshipTypeId().toString()));
        if (type.getStatus() == Status.DELETED) {
            throw new BusinessException("Relationship type is deleted");
        }

        // Check for duplicates
        boolean exists = entityRelationshipRepository.existsBySubjectEntityIdAndRelationshipTypeIdAndObjectEntityIdAndStatusNot(
                request.getSubjectEntityId(), request.getRelationshipTypeId(), request.getObjectEntityId(), Status.DELETED);
        if (exists) {
            throw new DuplicateResourceException("EntityRelationship", "combination", "subject, type, object");
        }

        EntityRelationship relationship = new EntityRelationship();
        relationship.setSubjectEntity(subject);
        relationship.setRelationshipType(type);
        relationship.setObjectEntity(object);
        relationship.setStatus(Status.ACTIVE);

        return entityRelationshipRepository.save(relationship);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EntityRelationship relationship = entityRelationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EntityRelationship", "id", id.toString()));

        if (relationship.getStatus() == Status.DELETED) {
            throw new BusinessException("Relationship is already deleted");
        }

        relationship.setStatus(Status.DELETED);
        entityRelationshipRepository.save(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityRelationship> listRelationships(UUID subjectId, UUID objectId, UUID entityId) {
        if (entityId != null) {
            return entityRelationshipRepository.findByEntityIdAndStatusNot(entityId, Status.DELETED);
        } else if (subjectId != null) {
            return entityRelationshipRepository.findBySubjectEntityIdAndStatusNot(subjectId, Status.DELETED);
        } else if (objectId != null) {
            return entityRelationshipRepository.findByObjectEntityIdAndStatusNot(objectId, Status.DELETED);
        } else {
            return entityRelationshipRepository.findAllActive(Status.DELETED);
        }
    }
}
