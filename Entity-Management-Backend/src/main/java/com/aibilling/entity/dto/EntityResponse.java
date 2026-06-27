package com.aibilling.entity.dto;

import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.EntityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing an Entity's full state.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse {

    private UUID id;
    private EntityCategory entityCategory;
    private List<String> entityTypeCodes;
    private OrganizationDetailsDto organizationDetails;
    private PersonDetailsDto personDetails;
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public EntityCategory getEntityCategory() { return entityCategory; }
    public void setEntityCategory(EntityCategory entityCategory) { this.entityCategory = entityCategory; }
    public List<String> getEntityTypeCodes() { return entityTypeCodes; }
    public void setEntityTypeCodes(List<String> entityTypeCodes) { this.entityTypeCodes = entityTypeCodes; }
    public OrganizationDetailsDto getOrganizationDetails() { return organizationDetails; }
    public void setOrganizationDetails(OrganizationDetailsDto organizationDetails) { this.organizationDetails = organizationDetails; }
    public PersonDetailsDto getPersonDetails() { return personDetails; }
    public void setPersonDetails(PersonDetailsDto personDetails) { this.personDetails = personDetails; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
