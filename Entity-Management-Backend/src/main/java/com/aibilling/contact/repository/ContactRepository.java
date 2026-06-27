package com.aibilling.contact.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.contact.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c LEFT JOIN FETCH c.contactType WHERE c.account.id = :accountId AND c.status != :status")
    List<Contact> findByAccountIdAndStatusNot(@Param("accountId") UUID accountId, @Param("status") Status status);

    @Query("SELECT c FROM Contact c LEFT JOIN FETCH c.contactType WHERE c.account.id IN :accountIds AND c.status != :status")
    List<Contact> findByAccountIdInAndStatusNot(@Param("accountIds") List<UUID> accountIds, @Param("status") Status status);

    long countByAccountIdAndStatusNot(UUID accountId, Status status);
}
