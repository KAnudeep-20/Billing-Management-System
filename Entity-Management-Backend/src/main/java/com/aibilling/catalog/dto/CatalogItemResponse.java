package com.aibilling.catalog.dto;

import com.aibilling.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Catalog Item.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemResponse {

    private UUID id;
    private String itemNumber;
    private String itemName;
    private String description;
    private UUID categoryId;
    private String categoryName;
    private UUID primaryUomId;
    private String primaryUomCode;
    private BigDecimal listPrice;
    private boolean isStocked;
    private boolean isInventoryTracked;
    private boolean isService;
    private boolean isSellable;
    private boolean isPurchasable;
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getItemNumber() { return itemNumber; }
    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public UUID getPrimaryUomId() { return primaryUomId; }
    public void setPrimaryUomId(UUID primaryUomId) { this.primaryUomId = primaryUomId; }
    public String getPrimaryUomCode() { return primaryUomCode; }
    public void setPrimaryUomCode(String primaryUomCode) { this.primaryUomCode = primaryUomCode; }
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
