package com.aibilling.common.service;

import com.aibilling.audit.BaseEntity;
import com.aibilling.common.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Base service interface defining common business operations for all domain entities.
 *
 * <p>Domain services should extend this interface:
 * <pre>
 * public interface EntityService extends BaseService&lt;MyEntity&gt; {
 *     // domain-specific business methods
 * }
 * </pre>
 *
 * @param <T> the entity type (must extend {@link BaseEntity})
 */
public interface BaseService<T extends BaseEntity> {

    /**
     * Finds an entity by its UUID.
     *
     * @param id the entity ID
     * @return the entity
     * @throws com.aibilling.exception.ResourceNotFoundException if not found
     */
    T findById(UUID id);

    /**
     * Returns a paginated list of all entities.
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Returns all active entities (non-paginated).
     */
    List<T> findAllActive();

    /**
     * Returns a paginated list of active entities.
     */
    Page<T> findAllActive(Pageable pageable);

    /**
     * Creates a new entity.
     *
     * @param entity the entity to persist
     * @return the persisted entity with generated ID
     */
    T create(T entity);

    /**
     * Updates an existing entity.
     *
     * @param id     the ID of the entity to update
     * @param entity the updated entity data
     * @return the updated entity
     */
    T update(UUID id, T entity);

    /**
     * Hard-deletes an entity by ID.
     *
     * @param id the entity ID
     */
    void deleteById(UUID id);

    /**
     * Soft-deletes an entity by setting its status to {@link Status#DELETED}.
     *
     * @param id the entity ID
     */
    void softDelete(UUID id);

}
