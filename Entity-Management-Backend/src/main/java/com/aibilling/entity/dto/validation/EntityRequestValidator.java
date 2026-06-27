package com.aibilling.entity.dto.validation;

import com.aibilling.entity.dto.EntityRequestDto;
import com.aibilling.entity.dto.OrganizationDetailsDto;
import com.aibilling.entity.dto.PersonDetailsDto;
import com.aibilling.entity.model.EntityCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates Entity requests to ensure mandatory fields are populated based on the Entity Category.
 */
public class EntityRequestValidator implements ConstraintValidator<ValidEntityRequest, EntityRequestDto> {

    @Override
    public boolean isValid(EntityRequestDto request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        if (request.getEntityCategory() == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (request.getEntityCategory() == EntityCategory.ORGANIZATION) {
            OrganizationDetailsDto org = request.getOrganizationDetails();
            if (org == null) {
                context.buildConstraintViolationWithTemplate("Organization details are required for ORGANIZATION category")
                        .addPropertyNode("organizationDetails")
                        .addConstraintViolation();
                isValid = false;
            } else {
                if (org.getOrganizationName() == null || org.getOrganizationName().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Organization name is required")
                            .addPropertyNode("organizationDetails.organizationName")
                            .addConstraintViolation();
                    isValid = false;
                }
                if (org.getTin() == null || org.getTin().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("TIN is required")
                            .addPropertyNode("organizationDetails.tin")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        } else if (request.getEntityCategory() == EntityCategory.PERSON) {
            PersonDetailsDto person = request.getPersonDetails();
            if (person == null) {
                context.buildConstraintViolationWithTemplate("Person details are required for PERSON category")
                        .addPropertyNode("personDetails")
                        .addConstraintViolation();
                isValid = false;
            } else {
                if (person.getFullName() == null || person.getFullName().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Full name is required")
                            .addPropertyNode("personDetails.fullName")
                            .addConstraintViolation();
                    isValid = false;
                }
                if (person.getIdentificationType() == null || person.getIdentificationType().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Identification type is required")
                            .addPropertyNode("personDetails.identificationType")
                            .addConstraintViolation();
                    isValid = false;
                }
                if (person.getIdentificationNumber() == null || person.getIdentificationNumber().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Identification number is required")
                            .addPropertyNode("personDetails.identificationNumber")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }

        return isValid;
    }

}
