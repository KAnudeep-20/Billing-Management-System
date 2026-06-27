package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.SiteUse;
import com.aibilling.setup.repository.SiteUseRepository;
import com.aibilling.setup.service.SiteUseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link SiteUse} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SiteUseServiceImpl extends BaseServiceImpl<SiteUse> implements SiteUseService {

    private final SiteUseRepository siteUseRepository;

    public SiteUseServiceImpl(SiteUseRepository repository) {
        super(repository, "SiteUse");
        this.siteUseRepository = repository;
    }

    @Override
    @Transactional
    public SiteUse create(SiteUse entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public SiteUse update(UUID id, SiteUse entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? siteUseRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : siteUseRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("SiteUse", "code", code);
        }

        boolean nameExists = (id == null)
                ? siteUseRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : siteUseRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("SiteUse", "name", name);
        }
    }

}
