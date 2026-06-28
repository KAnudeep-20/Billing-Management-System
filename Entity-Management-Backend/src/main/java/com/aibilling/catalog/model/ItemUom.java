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
 * JPA entity mapping a Catalog Item to secondary UOM conversions.
 */
@Entity
@Table(name = "item_uoms")
@SQLDelete(sql = "UPDATE item_uoms SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private CatalogItem item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uom_id", nullable = false)
    private Uom uom;

    @Column(name = "conversion_factor", nullable = false, precision = 19, scale = 6)
    private BigDecimal conversionFactor;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    public CatalogItem getItem() { return item; }
    public void setItem(CatalogItem item) { this.item = item; }
    public Uom getUom() { return uom; }
    public void setUom(Uom uom) { this.uom = uom; }
    public BigDecimal getConversionFactor() { return conversionFactor; }
    public void setConversionFactor(BigDecimal conversionFactor) { this.conversionFactor = conversionFactor; }
    public boolean getIsDefault() { return isDefault; }
    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
}
