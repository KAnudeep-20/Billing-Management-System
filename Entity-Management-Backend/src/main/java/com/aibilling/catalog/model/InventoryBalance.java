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
 * JPA entity representing an Inventory Balance record.
 * Tracks stock quantities of catalog items in warehouses.
 * quantityOnHand - reservedQty = availableQty.
 */
@Entity
@Table(name = "inventory_balances")
@SQLDelete(sql = "UPDATE inventory_balances SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBalance extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private CatalogItem item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_on_hand", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(name = "reserved_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(name = "available_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableQty = BigDecimal.ZERO;

    public CatalogItem getItem() { return item; }
    public void setItem(CatalogItem item) { this.item = item; }
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public BigDecimal getQuantityOnHand() { return quantityOnHand; }
    
    public void setQuantityOnHand(BigDecimal quantityOnHand) {
        this.quantityOnHand = quantityOnHand != null ? quantityOnHand : BigDecimal.ZERO;
        recalculateAvailable();
    }
    
    public BigDecimal getReservedQty() { return reservedQty; }
    
    public void setReservedQty(BigDecimal reservedQty) {
        this.reservedQty = reservedQty != null ? reservedQty : BigDecimal.ZERO;
        recalculateAvailable();
    }
    
    public BigDecimal getAvailableQty() { return availableQty; }
    
    public void setAvailableQty(BigDecimal availableQty) {
        // availableQty is calculated, but keeping setter for mapping
        this.availableQty = availableQty != null ? availableQty : BigDecimal.ZERO;
    }

    private void recalculateAvailable() {
        BigDecimal qoh = this.quantityOnHand != null ? this.quantityOnHand : BigDecimal.ZERO;
        BigDecimal res = this.reservedQty != null ? this.reservedQty : BigDecimal.ZERO;
        this.availableQty = qoh.subtract(res);
    }
}
