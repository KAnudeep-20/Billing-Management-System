package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.EntityType;
import com.aibilling.setup.repository.EntityTypeRepository;
import com.aibilling.setup.service.EntityTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link EntityType} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class EntityTypeServiceImpl extends BaseServiceImpl<EntityType> implements EntityTypeService {

    private final EntityTypeRepository entityTypeRepository;

    public EntityTypeServiceImpl(EntityTypeRepository repository) {
        super(repository, "EntityType");
        this.entityTypeRepository = repository;
    }

    @Override
    @Transactional
    public EntityType create(EntityType entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public EntityType update(UUID id, EntityType entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? entityTypeRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : entityTypeRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("EntityType", "code", code);
        }

        boolean nameExists = (id == null)
                ? entityTypeRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : entityTypeRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("EntityType", "name", name);
        }
    }

}
