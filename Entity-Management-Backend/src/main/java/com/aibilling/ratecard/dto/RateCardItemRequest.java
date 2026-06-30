package com.aibilling.ratecard.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCardItemRequest {

    @NotNull(message = "Catalog Item ID is required")
    private UUID catalogItemId;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0001", message = "Unit price must be greater than zero")
    private BigDecimal unitPrice;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;

    public UUID getCatalogItemId() { return catalogItemId; }
    public void setCatalogItemId(UUID catalogItemId) { this.catalogItemId = catalogItemId; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
