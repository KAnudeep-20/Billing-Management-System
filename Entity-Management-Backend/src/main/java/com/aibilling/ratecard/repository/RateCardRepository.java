package com.aibilling.ratecard.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.ratecard.model.RateCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RateCardRepository extends BaseRepository<RateCard> {

    boolean existsByRateCardCodeAndStatusNot(String code, Status status);
    boolean existsByRateCardCodeAndStatusNotAndIdNot(String code, Status status, UUID id);

    boolean existsByRateCardNameAndStatusNot(String name, Status status);
    boolean existsByRateCardNameAndStatusNotAndIdNot(String name, Status status, UUID id);

    @Query("SELECT r FROM RateCard r WHERE r.status != 'DELETED' " +
            "AND (:code IS NULL OR :code = '' OR LOWER(r.rateCardCode) LIKE LOWER(CONCAT('%', :code, '%'))) " +
            "AND (:name IS NULL OR :name = '' OR LOWER(r.rateCardName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<RateCard> searchRateCards(
            @Param("code") String code,
            @Param("name") String name,
            @Param("status") Status status,
            Pageable pageable);
}
