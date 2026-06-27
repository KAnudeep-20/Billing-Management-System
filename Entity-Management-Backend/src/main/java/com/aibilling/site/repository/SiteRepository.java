package com.aibilling.site.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.site.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteRepository extends JpaRepository<Site, UUID> {
    
    @Query("SELECT DISTINCT s FROM Site s LEFT JOIN FETCH s.siteUses su LEFT JOIN FETCH su.siteUse WHERE s.account.id = :accountId AND s.status != :status")
    List<Site> findByAccountIdAndStatusNot(@Param("accountId") UUID accountId, @Param("status") Status status);

    @Query("SELECT DISTINCT s FROM Site s LEFT JOIN FETCH s.siteUses su LEFT JOIN FETCH su.siteUse WHERE s.account.id IN :accountIds AND s.status != :status")
    List<Site> findByAccountIdInAndStatusNot(@Param("accountIds") List<UUID> accountIds, @Param("status") Status status);
    
    long countByAccountIdAndStatusNot(UUID accountId, Status status);
}
