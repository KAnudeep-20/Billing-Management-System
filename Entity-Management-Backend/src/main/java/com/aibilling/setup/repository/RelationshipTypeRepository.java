package com.aibilling.setup.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.setup.model.RelationshipType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link RelationshipType} entities.
 */
@Repository
public interface RelationshipTypeRepository extends BaseRepository<RelationshipType> {

    boolean existsByCodeAndStatusNot(String code, Status status);

    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    boolean existsByNameAndStatusNot(String name, Status status);

    boolean existsByNameAndStatusNotAndIdNot(String name, Status status, UUID id);

}
