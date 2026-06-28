package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.InventoryTransaction;
import com.aibilling.catalog.model.InventoryTransactionType;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link InventoryTransaction} entities.
 */
@Repository
public interface InventoryTransactionRepository extends BaseRepository<InventoryTransaction> {

    Page<InventoryTransaction> findByItemIdAndStatus(UUID itemId, Status status, Pageable pageable);

    Page<InventoryTransaction> findByWarehouseIdAndStatus(UUID warehouseId, Status status, Pageable pageable);

    Page<InventoryTransaction> findByItemIdAndWarehouseIdAndStatus(UUID itemId, UUID warehouseId, Status status, Pageable pageable);

    List<InventoryTransaction> findByItemIdAndTransactionTypeAndStatus(UUID itemId, InventoryTransactionType transactionType, Status status);
}
