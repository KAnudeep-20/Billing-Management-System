package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.InventoryBalance;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link InventoryBalance} entities.
 */
@Repository
public interface InventoryBalanceRepository extends BaseRepository<InventoryBalance> {

    Optional<InventoryBalance> findByItemIdAndWarehouseIdAndStatus(UUID itemId, UUID warehouseId, Status status);

    List<InventoryBalance> findByItemIdAndStatus(UUID itemId, Status status);

    List<InventoryBalance> findByWarehouseIdAndStatus(UUID warehouseId, Status status);

    boolean existsByItemIdAndWarehouseIdAndStatusNot(UUID itemId, UUID warehouseId, Status status);
}
