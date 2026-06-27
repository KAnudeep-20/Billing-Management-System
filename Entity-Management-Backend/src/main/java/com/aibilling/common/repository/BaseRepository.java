package com.aibilling.common.repository;

import com.aibilling.audit.BaseEntity;
import com.aibilling.common.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository providing common data-access operations for all domain entities.
 *
 * <p>Extends {@link JpaRepository} for standard CRUD and {@link JpaSpecificationExecutor}
 * for dynamic, type-safe queries via the Criteria API.
 *
 * <p>Domain repositories should extend this interface:
 * <pre>
 * public interface EntityRepository extends BaseRepository&lt;MyEntity&gt; {
 *     // domain-specific query methods
 * }
 * </pre>
 *
 * @param <T> the entity type (must extend {@link BaseEntity})
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, UUID>,
        JpaSpecificationExecutor<T> {

    /**
     * Finds all entities with the given status.
     */
    List<T> findByStatus(Status status);

    /**
     * Finds all entities with the given status (paginated).
     */
    Page<T> findByStatus(Status status, Pageable pageable);

    /**
     * Finds an entity by ID only if it has the specified status.
     */
    Optional<T> findByIdAndStatus(UUID id, Status status);

    /**
     * Checks whether an entity with the given ID and status exists.
     */
    boolean existsByIdAndStatus(UUID id, Status status);

}
