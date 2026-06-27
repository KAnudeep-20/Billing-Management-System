package com.aibilling.contact.service.impl;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.contact.model.Contact;
import com.aibilling.contact.repository.ContactRepository;
import com.aibilling.contact.service.ContactService;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.setup.model.ContactType;
import com.aibilling.setup.repository.ContactTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final AccountRepository accountRepository;
    private final ContactTypeRepository contactTypeRepository;

    public ContactServiceImpl(ContactRepository contactRepository,
                              AccountRepository accountRepository,
                              ContactTypeRepository contactTypeRepository) {
        this.contactRepository = contactRepository;
        this.accountRepository = accountRepository;
        this.contactTypeRepository = contactTypeRepository;
    }

    @Override
    @Transactional
    public Contact create(Contact contact, UUID accountId, UUID contactTypeId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        if (account.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot add contact to a deleted Account");
        }

        contact.setAccount(account);
        contact.setPlaceholder(false);

        if (contactTypeId != null) {
            ContactType ct = contactTypeRepository.findById(contactTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("ContactType", "id", contactTypeId.toString()));
            contact.setContactType(ct);
        }

        // Soft-delete any existing placeholder contacts for this account
        List<Contact> existing = contactRepository.findByAccountIdAndStatusNot(accountId, Status.DELETED);
        for (Contact c : existing) {
            if (c.isPlaceholder()) {
                c.setStatus(Status.DELETED);
                contactRepository.save(c);
            }
        }

        return contactRepository.save(contact);
    }

    @Override
    @Transactional
    public Contact update(UUID id, Contact contactUpdate, UUID contactTypeId) {
        Contact existing = findById(id);

        if (existing.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot update a deleted contact");
        }

        existing.setFirstName(contactUpdate.getFirstName());
        existing.setLastName(contactUpdate.getLastName());
        existing.setEmail(contactUpdate.getEmail());
        existing.setPhone(contactUpdate.getPhone());
        existing.setRole(contactUpdate.getRole());
        existing.setDesignation(contactUpdate.getDesignation());
        existing.setStatus(contactUpdate.getStatus());

        // If updating a placeholder, mark it as no longer a placeholder
        if (existing.isPlaceholder()) {
            existing.setPlaceholder(false);
        }

        if (contactTypeId != null) {
            ContactType ct = contactTypeRepository.findById(contactTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("ContactType", "id", contactTypeId.toString()));
            existing.setContactType(ct);
        } else {
            existing.setContactType(null);
        }

        return contactRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Contact contact = findById(id);

        if (contact.getStatus() == Status.DELETED) {
            return;
        }

        contact.setStatus(Status.DELETED);
        contactRepository.save(contact);

        // If there are no more active contacts, create a placeholder
        List<Contact> activeContacts = contactRepository.findByAccountIdAndStatusNot(contact.getAccount().getId(), Status.DELETED);
        if (activeContacts.isEmpty()) {
            createPlaceholderContact(contact.getAccount().getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Contact findById(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findByAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", "id", accountId.toString());
        }
        return contactRepository.findByAccountIdAndStatusNot(accountId, Status.DELETED);
    }

    @Override
    @Transactional
    public Contact createPlaceholderContact(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        Contact placeholder = new Contact();
        placeholder.setAccount(account);
        placeholder.setFirstName("Missing Information");
        placeholder.setLastName("");
        placeholder.setEmail("");
        placeholder.setPhone("");
        placeholder.setRole("");
        placeholder.setDesignation("");
        placeholder.setPlaceholder(true);
        placeholder.setStatus(Status.ACTIVE);

        return contactRepository.save(placeholder);
    }
}
