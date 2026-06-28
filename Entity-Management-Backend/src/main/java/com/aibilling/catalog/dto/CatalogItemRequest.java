package com.aibilling.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
 * Request DTO for creating or updating a Catalog Item.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemRequest {

    @NotBlank(message = "Item number is required")
    @Size(max = 50, message = "Item number must not exceed 50 characters")
    private String itemNumber;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    private String itemName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Primary UOM ID is required")
    private UUID primaryUomId;

    @NotNull(message = "List price is required")
    @DecimalMin(value = "0.0", message = "List price must be zero or positive")
    private BigDecimal listPrice;

    private boolean isStocked;
    private boolean isInventoryTracked;
    private boolean isService;
    @Builder.Default
    private boolean isSellable = true;
    @Builder.Default
    private boolean isPurchasable = true;

    public String getItemNumber() { return itemNumber; }
    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    public UUID getPrimaryUomId() { return primaryUomId; }
    public void setPrimaryUomId(UUID primaryUomId) { this.primaryUomId = primaryUomId; }
    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }
    public boolean getIsStocked() { return isStocked; }
    public void setIsStocked(boolean isStocked) { this.isStocked = isStocked; }
    public boolean getIsInventoryTracked() { return isInventoryTracked; }
    public void setIsInventoryTracked(boolean isInventoryTracked) { this.isInventoryTracked = isInventoryTracked; }
    public boolean getIsService() { return isService; }
    public void setIsService(boolean isService) { this.isService = isService; }
    public boolean getIsSellable() { return isSellable; }
    public void setIsSellable(boolean isSellable) { this.isSellable = isSellable; }
    public boolean getIsPurchasable() { return isPurchasable; }
    public void setIsPurchasable(boolean isPurchasable) { this.isPurchasable = isPurchasable; }
}
