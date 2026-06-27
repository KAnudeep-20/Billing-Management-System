package com.aibilling.setup.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.setup.model.ContactType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link ContactType} entities.
 */
@Repository
public interface ContactTypeRepository extends BaseRepository<ContactType> {

    boolean existsByCodeAndStatusNot(String code, Status status);

    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    boolean existsByNameAndStatusNot(String name, Status status);

    boolean existsByNameAndStatusNotAndIdNot(String name, Status status, UUID id);

}
