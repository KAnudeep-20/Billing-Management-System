package com.aibilling.entity.service;

import com.aibilling.common.service.BaseService;
import com.aibilling.entity.model.Entity;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing Entity aggregate roots.
 */
public interface EntityService extends BaseService<Entity> {

    /**
     * Creates a new Entity with associated role types.
     *
     * @param entity          the entity to create
     * @param entityTypeCodes the list of entity type codes to associate
     * @return the created entity
     */
    Entity create(Entity entity, List<String> entityTypeCodes);

    /**
     * Updates an existing Entity with updated details and role types.
     *
     * @param id              the ID of the entity to update
     * @param entity          the updated entity data
     * @param entityTypeCodes the updated list of entity type codes to associate
     * @return the updated entity
     */
    Entity update(UUID id, Entity entity, List<String> entityTypeCodes);

}
