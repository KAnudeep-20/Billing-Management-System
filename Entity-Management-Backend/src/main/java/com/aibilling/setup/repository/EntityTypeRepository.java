package com.aibilling.setup.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.setup.model.EntityType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link EntityType} entities.
 */
@Repository
public interface EntityTypeRepository extends BaseRepository<EntityType> {

    boolean existsByCodeAndStatusNot(String code, Status status);

    java.util.Optional<EntityType> findByCodeAndStatus(String code, Status status);

    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    boolean existsByNameAndStatusNot(String name, Status status);

    boolean existsByNameAndStatusNotAndIdNot(String name, Status status, UUID id);

}
