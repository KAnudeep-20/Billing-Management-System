package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.model.Uom;
import com.aibilling.catalog.repository.UomRepository;
import com.aibilling.catalog.service.UomService;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for managing UOM entities.
 */
@Service
@Transactional(readOnly = true)
public class UomServiceImpl extends BaseServiceImpl<Uom> implements UomService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UomServiceImpl.class);

    private final UomRepository uomRepository;

    public UomServiceImpl(UomRepository uomRepository) {
        super(uomRepository, "UOM");
        this.uomRepository = uomRepository;
    }

    @Override
    @Transactional
    public Uom create(Uom uom) {
        if (uomRepository.existsByCodeAndStatusNot(uom.getCode(), Status.DELETED)) {
            throw new DuplicateResourceException("UOM", "code", uom.getCode());
        }
        uom.setStatus(Status.ACTIVE);
        Uom saved = uomRepository.save(uom);
        log.info("UOM created with id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }

    @Override
    @Transactional
    public Uom update(UUID id, Uom uom) {
        Uom existing = findById(id);
        if (uomRepository.existsByCodeAndStatusNotAndIdNot(uom.getCode(), Status.DELETED, id)) {
            throw new DuplicateResourceException("UOM", "code", uom.getCode());
        }
        existing.setCode(uom.getCode());
        existing.setDescription(uom.getDescription());
        Uom saved = uomRepository.save(existing);
        log.info("UOM updated with id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }
}
