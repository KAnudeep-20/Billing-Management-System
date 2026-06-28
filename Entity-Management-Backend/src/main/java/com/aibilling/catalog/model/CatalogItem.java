package com.aibilling.catalog.model;

import com.aibilling.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;

/**
 * JPA entity representing a Catalog Item.
 */
@Entity
@Table(name = "catalog_items")
@SQLDelete(sql = "UPDATE catalog_items SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItem extends BaseEntity {

    @Column(name = "item_number", nullable = false, length = 50)
    private String itemNumber;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CatalogCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_uom_id", nullable = false)
    private Uom primaryUom;

    @Column(name = "list_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal listPrice = BigDecimal.ZERO;

    @Column(name = "is_stocked", nullable = false)
    private boolean isStocked = false;

    @Column(name = "is_inventory_tracked", nullable = false)
    private boolean isInventoryTracked = false;

    @Column(name = "is_service", nullable = false)
    private boolean isService = false;

    @Column(name = "is_sellable", nullable = false)
    private boolean isSellable = true;

    @Column(name = "is_purchasable", nullable = false)
    private boolean isPurchasable = true;

    public String getItemNumber() { return itemNumber; }
    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CatalogCategory getCategory() { return category; }
    public void setCategory(CatalogCategory category) { this.category = category; }
    public Uom getPrimaryUom() { return primaryUom; }
    public void setPrimaryUom(Uom primaryUom) { this.primaryUom = primaryUom; }
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
