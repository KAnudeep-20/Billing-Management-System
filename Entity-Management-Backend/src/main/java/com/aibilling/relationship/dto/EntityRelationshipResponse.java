package com.aibilling.relationship.dto;

import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.EntityCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public class EntityRelationshipResponse {

    private UUID id;
    private UUID subjectEntityId;
    private EntityCategory subjectEntityCategory;
    private String subjectEntityName;

    private UUID relationshipTypeId;
    private String relationshipTypeCode;
    private String relationshipTypeName;

    private UUID objectEntityId;
    private EntityCategory objectEntityCategory;
    private String objectEntityName;

    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSubjectEntityId() {
        return subjectEntityId;
    }

    public void setSubjectEntityId(UUID subjectEntityId) {
        this.subjectEntityId = subjectEntityId;
    }

    public EntityCategory getSubjectEntityCategory() {
        return subjectEntityCategory;
    }

    public void setSubjectEntityCategory(EntityCategory subjectEntityCategory) {
        this.subjectEntityCategory = subjectEntityCategory;
    }

    public String getSubjectEntityName() {
        return subjectEntityName;
    }

    public void setSubjectEntityName(String subjectEntityName) {
        this.subjectEntityName = subjectEntityName;
    }

    public UUID getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(UUID relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }

    public String getRelationshipTypeCode() {
        return relationshipTypeCode;
    }

    public void setRelationshipTypeCode(String relationshipTypeCode) {
        this.relationshipTypeCode = relationshipTypeCode;
    }

    public String getRelationshipTypeName() {
        return relationshipTypeName;
    }

    public void setRelationshipTypeName(String relationshipTypeName) {
        this.relationshipTypeName = relationshipTypeName;
    }

    public UUID getObjectEntityId() {
        return objectEntityId;
    }

    public void setObjectEntityId(UUID objectEntityId) {
        this.objectEntityId = objectEntityId;
    }

    public EntityCategory getObjectEntityCategory() {
        return objectEntityCategory;
    }

    public void setObjectEntityCategory(EntityCategory objectEntityCategory) {
        this.objectEntityCategory = objectEntityCategory;
    }

    public String getObjectEntityName() {
        return objectEntityName;
    }

    public void setObjectEntityName(String objectEntityName) {
        this.objectEntityName = objectEntityName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
