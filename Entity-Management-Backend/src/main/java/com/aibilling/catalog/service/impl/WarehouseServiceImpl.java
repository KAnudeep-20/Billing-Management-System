package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.model.Warehouse;
import com.aibilling.catalog.repository.WarehouseRepository;
import com.aibilling.catalog.service.WarehouseService;
import com.aibilling.common.enums.Status;
import com.aibilling.common.service.impl.BaseServiceImpl;
import com.aibilling.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for managing Warehouse entities.
 */
@Service
@Transactional(readOnly = true)
public class WarehouseServiceImpl extends BaseServiceImpl<Warehouse> implements WarehouseService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        super(warehouseRepository, "Warehouse");
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    @Transactional
    public Warehouse create(Warehouse warehouse) {
        if (warehouseRepository.existsByCodeAndStatusNot(warehouse.getCode(), Status.DELETED)) {
            throw new DuplicateResourceException("Warehouse", "code", warehouse.getCode());
        }
        warehouse.setStatus(Status.ACTIVE);
        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created with id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }

    @Override
    @Transactional
    public Warehouse update(UUID id, Warehouse warehouse) {
        Warehouse existing = findById(id);
        if (warehouseRepository.existsByCodeAndStatusNotAndIdNot(warehouse.getCode(), Status.DELETED, id)) {
            throw new DuplicateResourceException("Warehouse", "code", warehouse.getCode());
        }
        existing.setCode(warehouse.getCode());
        existing.setName(warehouse.getName());
        existing.setAddress(warehouse.getAddress());
        Warehouse saved = warehouseRepository.save(existing);
        log.info("Warehouse updated with id={}, code={}", saved.getId(), saved.getCode());
        return saved;
    }
}
