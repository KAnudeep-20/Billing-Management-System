package com.aibilling.catalog.repository;

import com.aibilling.catalog.model.CatalogCategory;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link CatalogCategory} entities.
 */
@Repository
public interface CatalogCategoryRepository extends BaseRepository<CatalogCategory> {

    Optional<CatalogCategory> findByCodeAndStatus(String code, Status status);

    boolean existsByCodeAndStatusNot(String code, Status status);

    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    boolean existsByNameAndStatusNot(String name, Status status);

    boolean existsByNameAndStatusNotAndIdNot(String name, Status status, UUID id);

    boolean existsByParentCategoryIdAndStatusNot(UUID parentId, Status status);
}
