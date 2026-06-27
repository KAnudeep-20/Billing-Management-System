package com.aibilling.account.repository;

import com.aibilling.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    List<Account> findByEntityIdAndStatusNot(UUID entityId, com.aibilling.common.enums.Status status);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.paymentTerm LEFT JOIN FETCH a.billingCycle WHERE a.entity.id = :entityId AND a.status != :status")
    List<Account> findByEntityIdAndStatusNotWithDetails(@Param("entityId") UUID entityId, @Param("status") com.aibilling.common.enums.Status status);
    
    long countByEntityIdAndStatusNot(UUID entityId, com.aibilling.common.enums.Status status);
}
