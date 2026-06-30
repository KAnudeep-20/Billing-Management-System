package com.aibilling.ratecard.dto;

import com.aibilling.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCardItemResponse {
    private UUID id;
    private UUID rateCardId;
    private UUID catalogItemId;
    private String catalogItemNumber;
    private String catalogItemName;
    private UUID primaryUomId;
    private String primaryUomCode;
    private BigDecimal unitPrice;
    private String remarks;
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRateCardId() { return rateCardId; }
    public void setRateCardId(UUID rateCardId) { this.rateCardId = rateCardId; }

    public UUID getCatalogItemId() { return catalogItemId; }
    public void setCatalogItemId(UUID catalogItemId) { this.catalogItemId = catalogItemId; }

    public String getCatalogItemNumber() { return catalogItemNumber; }
    public void setCatalogItemNumber(String catalogItemNumber) { this.catalogItemNumber = catalogItemNumber; }

    public String getCatalogItemName() { return catalogItemName; }
    public void setCatalogItemName(String catalogItemName) { this.catalogItemName = catalogItemName; }

    public UUID getPrimaryUomId() { return primaryUomId; }
    public void setPrimaryUomId(UUID primaryUomId) { this.primaryUomId = primaryUomId; }

    public String getPrimaryUomCode() { return primaryUomCode; }
    public void setPrimaryUomCode(String primaryUomCode) { this.primaryUomCode = primaryUomCode; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
