package com.aibilling.entity.dto;

import com.aibilling.entity.dto.validation.ValidEntityRequest;
import com.aibilling.entity.model.EntityCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request DTO for creating a new Entity.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidEntityRequest
public class EntityCreateRequest implements EntityRequestDto {

    @NotNull(message = "Entity category is required")
    private EntityCategory entityCategory;

    @NotEmpty(message = "At least one entity type is required")
    private List<String> entityTypeCodes;

    private OrganizationDetailsDto organizationDetails;

    private PersonDetailsDto personDetails;

    public EntityCategory getEntityCategory() { return entityCategory; }
    public void setEntityCategory(EntityCategory entityCategory) { this.entityCategory = entityCategory; }
    public OrganizationDetailsDto getOrganizationDetails() { return organizationDetails; }
    public void setOrganizationDetails(OrganizationDetailsDto organizationDetails) { this.organizationDetails = organizationDetails; }
    public PersonDetailsDto getPersonDetails() { return personDetails; }
    public void setPersonDetails(PersonDetailsDto personDetails) { this.personDetails = personDetails; }
    public List<String> getEntityTypeCodes() { return entityTypeCodes; }
    public void setEntityTypeCodes(List<String> entityTypeCodes) { this.entityTypeCodes = entityTypeCodes; }

}
