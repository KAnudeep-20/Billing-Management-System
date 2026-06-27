package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.RelationshipType;
import com.aibilling.setup.repository.RelationshipTypeRepository;
import com.aibilling.setup.service.RelationshipTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link RelationshipType} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class RelationshipTypeServiceImpl extends BaseServiceImpl<RelationshipType> implements RelationshipTypeService {

    private final RelationshipTypeRepository relationshipTypeRepository;

    public RelationshipTypeServiceImpl(RelationshipTypeRepository repository) {
        super(repository, "RelationshipType");
        this.relationshipTypeRepository = repository;
    }

    @Override
    @Transactional
    public RelationshipType create(RelationshipType entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public RelationshipType update(UUID id, RelationshipType entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? relationshipTypeRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : relationshipTypeRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("RelationshipType", "code", code);
        }

        boolean nameExists = (id == null)
                ? relationshipTypeRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : relationshipTypeRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("RelationshipType", "name", name);
        }
    }

}
