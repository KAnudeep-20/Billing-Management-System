package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.ItemUom;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link ItemUom} entities.
 */
@Repository
public interface ItemUomRepository extends BaseRepository<ItemUom> {

    Optional<ItemUom> findByItemIdAndUomIdAndStatus(UUID itemId, UUID uomId, Status status);

    List<ItemUom> findByItemIdAndStatus(UUID itemId, Status status);

    boolean existsByItemIdAndUomIdAndStatusNot(UUID itemId, UUID uomId, Status status);

    boolean existsByItemIdAndUomIdAndStatusNotAndIdNot(UUID itemId, UUID uomId, Status status, UUID id);
}
