package com.aibilling.ratecard.repository;

import com.aibilling.common.repository.BaseRepository;
import com.aibilling.ratecard.model.EntityRateCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityRateCardRepository extends BaseRepository<EntityRateCard> {

    @Query("SELECT erc FROM EntityRateCard erc WHERE erc.entity.id = :entityId AND erc.activeFlag = true AND erc.status = 'ACTIVE'")
    Optional<EntityRateCard> findActiveAssignment(@Param("entityId") UUID entityId);

    @Query("SELECT erc FROM EntityRateCard erc " +
            "LEFT JOIN erc.entity e " +
            "LEFT JOIN e.details ed " +
            "JOIN erc.rateCard rc " +
            "WHERE erc.status != 'DELETED' " +
            "AND (:entityName IS NULL OR :entityName = '' OR LOWER(ed.organizationName) LIKE LOWER(CONCAT('%', :entityName, '%')) OR LOWER(ed.fullName) LIKE LOWER(CONCAT('%', :entityName, '%'))) " +
            "AND (:rateCardName IS NULL OR :rateCardName = '' OR LOWER(rc.rateCardName) LIKE LOWER(CONCAT('%', :rateCardName, '%')))")
    Page<EntityRateCard> searchAssignments(
            @Param("entityName") String entityName,
            @Param("rateCardName") String rateCardName,
            Pageable pageable);
}
