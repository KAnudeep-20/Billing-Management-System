package com.aibilling.setup.service.impl;

import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import com.aibilling.setup.model.PaymentTerm;
import com.aibilling.setup.repository.PaymentTermRepository;
import com.aibilling.setup.service.PaymentTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for {@link PaymentTerm} operations.
 * Extends {@link BaseServiceImpl} to inherit core CRUD functions.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class PaymentTermServiceImpl extends BaseServiceImpl<PaymentTerm> implements PaymentTermService {

    private final PaymentTermRepository paymentTermRepository;

    public PaymentTermServiceImpl(PaymentTermRepository repository) {
        super(repository, "PaymentTerm");
        this.paymentTermRepository = repository;
    }

    @Override
    @Transactional
    public PaymentTerm create(PaymentTerm entity) {
        validateUniqueness(null, entity.getCode(), entity.getName());
        return super.create(entity);
    }

    @Override
    @Transactional
    public PaymentTerm update(UUID id, PaymentTerm entity) {
        validateUniqueness(id, entity.getCode(), entity.getName());
        return super.update(id, entity);
    }

    private void validateUniqueness(UUID id, String code, String name) {
        boolean codeExists = (id == null)
                ? paymentTermRepository.existsByCodeAndStatusNot(code, Status.DELETED)
                : paymentTermRepository.existsByCodeAndStatusNotAndIdNot(code, Status.DELETED, id);

        if (codeExists) {
            throw new DuplicateResourceException("PaymentTerm", "code", code);
        }

        boolean nameExists = (id == null)
                ? paymentTermRepository.existsByNameAndStatusNot(name, Status.DELETED)
                : paymentTermRepository.existsByNameAndStatusNotAndIdNot(name, Status.DELETED, id);

        if (nameExists) {
            throw new DuplicateResourceException("PaymentTerm", "name", name);
        }
    }

}
