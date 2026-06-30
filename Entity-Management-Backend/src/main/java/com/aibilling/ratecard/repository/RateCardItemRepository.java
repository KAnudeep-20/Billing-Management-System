package com.aibilling.ratecard.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.ratecard.model.RateCardItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateCardItemRepository extends BaseRepository<RateCardItem> {

    List<RateCardItem> findByRateCardIdAndStatus(UUID rateCardId, Status status);

    boolean existsByRateCardIdAndCatalogItemIdAndStatusNot(UUID rateCardId, UUID catalogItemId, Status status);
    boolean existsByRateCardIdAndCatalogItemIdAndStatusNotAndIdNot(UUID rateCardId, UUID catalogItemId, Status status, UUID id);

    @Query("SELECT ri FROM RateCardItem ri JOIN ri.catalogItem ci WHERE ri.rateCard.id = :rateCardId AND ri.status != 'DELETED' " +
            "AND (:itemCode IS NULL OR :itemCode = '' OR LOWER(ci.itemNumber) LIKE LOWER(CONCAT('%', :itemCode, '%'))) " +
            "AND (:itemName IS NULL OR :itemName = '' OR LOWER(ci.itemName) LIKE LOWER(CONCAT('%', :itemName, '%')))")
    Page<RateCardItem> searchItems(
            @Param("rateCardId") UUID rateCardId,
            @Param("itemCode") String itemCode,
            @Param("itemName") String itemName,
            Pageable pageable);

    @Query("SELECT ri FROM RateCardItem ri WHERE ri.rateCard.id = :rateCardId AND ri.catalogItem.id = :catalogItemId AND ri.status = 'ACTIVE'")
    Optional<RateCardItem> findActiveByRateCardAndCatalogItem(@Param("rateCardId") UUID rateCardId, @Param("catalogItemId") UUID catalogItemId);
}
