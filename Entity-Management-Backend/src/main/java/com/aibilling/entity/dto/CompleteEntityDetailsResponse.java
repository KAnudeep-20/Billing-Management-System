package com.aibilling.entity.dto;

import com.aibilling.account.dto.AccountHierarchyResponse;
import com.aibilling.relationship.dto.EntityRelationshipResponse;

import java.util.List;

public class CompleteEntityDetailsResponse {

    private EntityResponse entitySummary;
    private List<EntityRelationshipResponse> relationships;
    private List<AccountHierarchyResponse> accounts;

    public EntityResponse getEntitySummary() {
        return entitySummary;
    }

    public void setEntitySummary(EntityResponse entitySummary) {
        this.entitySummary = entitySummary;
    }

    public List<EntityRelationshipResponse> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<EntityRelationshipResponse> relationships) {
        this.relationships = relationships;
    }

    public List<AccountHierarchyResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountHierarchyResponse> accounts) {
        this.accounts = accounts;
    }
}
