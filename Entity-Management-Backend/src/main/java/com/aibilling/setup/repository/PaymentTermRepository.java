package com.aibilling.setup.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.setup.model.PaymentTerm;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link PaymentTerm} entities.
 */
@Repository
public interface PaymentTermRepository extends BaseRepository<PaymentTerm> {

    /**
     * Checks if a record exists with the given code and is not deleted.
     */
    boolean existsByCodeAndStatusNot(String code, Status status);

    /**
     * Checks if a record exists with the given code, is not deleted, and is not the current record.
     */
    boolean existsByCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    /**
     * Checks if a record exists with the given name and is not deleted.
     */
    boolean existsByNameAndStatusNot(String name, Status status);

    /**
     * Checks if a record exists with the given name, is not deleted, and is not the current record.
     */
    boolean existsByNameAndStatusNotAndIdNot(String name, Status status, UUID id);

}
