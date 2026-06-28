package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.Warehouse;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Warehouse} entities.
 */
@Repository
public interface WarehouseRepository extends BaseRepository<Warehouse> {

    Optional<Warehouse> findByCodeAndStatus(String code, Status status);

    boolean existsByCodeAndStatusNot(String code, Status status);

    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);
}
