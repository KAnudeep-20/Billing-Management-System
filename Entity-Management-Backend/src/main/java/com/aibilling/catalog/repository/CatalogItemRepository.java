package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link CatalogItem} entities.
 */
@Repository
public interface CatalogItemRepository extends BaseRepository<CatalogItem> {

    Optional<CatalogItem> findByItemNumberAndStatus(String itemNumber, Status status);

    boolean existsByItemNumberAndStatusNot(String itemNumber, Status status);

    boolean existsByItemNumberAndStatusNotAndIdNot(String itemNumber, Status status, UUID id);

    boolean existsByCategoryIdAndStatusNot(UUID categoryId, Status status);
}
