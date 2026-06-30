package com.aibilling.ratecard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityRateCardRequest {

    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    @NotNull(message = "Rate Card ID is required")
    private UUID rateCardId;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Builder.Default
    private boolean activeFlag = true;

    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }

    public UUID getRateCardId() { return rateCardId; }
    public void setRateCardId(UUID rateCardId) { this.rateCardId = rateCardId; }

    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }

    public boolean isActiveFlag() { return activeFlag; }
    public void setActiveFlag(boolean activeFlag) { this.activeFlag = activeFlag; }
}
