package com.aibilling.entity.dto;

import com.aibilling.entity.model.EntityCategory;

/**
 * Shared interface for Entity request DTOs to enable uniform validation.
 */
public interface EntityRequestDto {

    EntityCategory getEntityCategory();

    OrganizationDetailsDto getOrganizationDetails();

    PersonDetailsDto getPersonDetails();

}
