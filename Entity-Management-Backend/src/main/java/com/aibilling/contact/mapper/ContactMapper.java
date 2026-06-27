package com.aibilling.contact.mapper;

import com.aibilling.common.enums.Status;
import com.aibilling.contact.dto.ContactCreateRequest;
import com.aibilling.contact.dto.ContactResponse;
import com.aibilling.contact.dto.ContactUpdateRequest;
import com.aibilling.contact.model.Contact;
import com.aibilling.setup.dto.LookupResponse;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public Contact toEntity(ContactCreateRequest request) {
        if (request == null) return null;

        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setRole(request.getRole());
        contact.setDesignation(request.getDesignation());
        contact.setStatus(Status.ACTIVE);
        return contact;
    }

    public Contact toEntity(ContactUpdateRequest request) {
        if (request == null) return null;

        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setRole(request.getRole());
        contact.setDesignation(request.getDesignation());
        contact.setStatus(request.getStatus());
        return contact;
    }

    public ContactResponse toResponse(Contact contact) {
        if (contact == null) return null;

        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        if (contact.getAccount() != null) {
            response.setAccountId(contact.getAccount().getId());
        }
        if (contact.getContactType() != null) {
            LookupResponse ct = new LookupResponse();
            ct.setId(contact.getContactType().getId());
            ct.setCode(contact.getContactType().getCode());
            ct.setName(contact.getContactType().getName());
            response.setContactType(ct);
        }
        response.setFirstName(contact.getFirstName());
        response.setLastName(contact.getLastName());
        response.setEmail(contact.getEmail());
        response.setPhone(contact.getPhone());
        response.setRole(contact.getRole());
        response.setDesignation(contact.getDesignation());
        response.setPlaceholder(contact.isPlaceholder());
        response.setStatus(contact.getStatus());
        response.setCreatedAt(contact.getCreatedAt());
        response.setUpdatedAt(contact.getUpdatedAt());
        return response;
    }
}
