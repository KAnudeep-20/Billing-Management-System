package com.aibilling.ratecard.model;

import com.aibilling.audit.BaseEntity;
import com.aibilling.entity.model.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "entity_rate_cards")
@SQLDelete(sql = "UPDATE entity_rate_cards SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityRateCard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private Entity entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_card_id", nullable = false)
    private RateCard rateCard;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "active_flag", nullable = false)
    private boolean activeFlag = true;

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }

    public RateCard getRateCard() { return rateCard; }
    public void setRateCard(RateCard rateCard) { this.rateCard = rateCard; }

    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }

    public boolean isActiveFlag() { return activeFlag; }
    public void setActiveFlag(boolean activeFlag) { this.activeFlag = activeFlag; }
}
