package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.ContactType;
import com.aibilling.setup.repository.ContactTypeRepository;
import com.aibilling.setup.service.ContactTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link ContactType} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ContactTypeServiceImpl extends BaseServiceImpl<ContactType> implements ContactTypeService {

    private final ContactTypeRepository contactTypeRepository;

    public ContactTypeServiceImpl(ContactTypeRepository repository) {
        super(repository, "ContactType");
        this.contactTypeRepository = repository;
    }

    @Override
    @Transactional
    public ContactType create(ContactType entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public ContactType update(UUID id, ContactType entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? contactTypeRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : contactTypeRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("ContactType", "code", code);
        }

        boolean nameExists = (id == null)
                ? contactTypeRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : contactTypeRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("ContactType", "name", name);
        }
    }

}
