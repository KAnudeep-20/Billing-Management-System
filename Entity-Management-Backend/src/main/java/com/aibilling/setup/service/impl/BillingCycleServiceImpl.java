package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.BillingCycle;
import com.aibilling.setup.repository.BillingCycleRepository;
import com.aibilling.setup.service.BillingCycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link BillingCycle} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BillingCycleServiceImpl extends BaseServiceImpl<BillingCycle> implements BillingCycleService {

    private final BillingCycleRepository billingCycleRepository;

    public BillingCycleServiceImpl(BillingCycleRepository repository) {
        super(repository, "BillingCycle");
        this.billingCycleRepository = repository;
    }

    @Override
    @Transactional
    public BillingCycle create(BillingCycle entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public BillingCycle update(UUID id, BillingCycle entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? billingCycleRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : billingCycleRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("BillingCycle", "code", code);
        }

        boolean nameExists = (id == null)
                ? billingCycleRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : billingCycleRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("BillingCycle", "name", name);
        }
    }

}
