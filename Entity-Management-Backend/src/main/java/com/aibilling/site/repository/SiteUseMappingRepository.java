package com.aibilling.site.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.site.model.SiteUseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SiteUseMappingRepository extends JpaRepository<SiteUseMapping, UUID> {
    
    List<SiteUseMapping> findBySiteIdAndStatusNot(UUID siteId, Status status);
    
    void deleteBySiteId(UUID siteId);
}
