package com.aibilling.catalog.dto;

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

/**
 * Request DTO for transferring stock between warehouses.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferRequest {

    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotNull(message = "Source warehouse ID is required")
    private UUID sourceWarehouseId;

    @NotNull(message = "Destination warehouse ID is required")
    private UUID destinationWarehouseId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotNull(message = "UOM ID is required")
    private UUID uomId;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;

    public UUID getItemId() { return itemId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }
    public UUID getSourceWarehouseId() { return sourceWarehouseId; }
    public void setSourceWarehouseId(UUID sourceWarehouseId) { this.sourceWarehouseId = sourceWarehouseId; }
    public UUID getDestinationWarehouseId() { return destinationWarehouseId; }
    public void setDestinationWarehouseId(UUID destinationWarehouseId) { this.destinationWarehouseId = destinationWarehouseId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public UUID getUomId() { return uomId; }
    public void setUomId(UUID uomId) { this.uomId = uomId; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
