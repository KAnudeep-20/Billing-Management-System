package com.aibilling.contact.service;

import com.aibilling.contact.model.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    Contact create(Contact contact, UUID accountId, UUID contactTypeId);

    Contact update(UUID id, Contact contact, UUID contactTypeId);

    void delete(UUID id);

    Contact findById(UUID id);

    List<Contact> findByAccountId(UUID accountId);

    /**
     * Creates a "Missing Information" placeholder contact for an account.
     * Called automatically when an account is created without contacts.
     */
    Contact createPlaceholderContact(UUID accountId);
}
