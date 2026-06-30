package com.aibilling.ratecard.model;

import com.aibilling.audit.BaseEntity;
import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.model.Uom;
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

@Entity
@Table(name = "rate_card_items")
@SQLDelete(sql = "UPDATE rate_card_items SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateCardItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_card_id", nullable = false)
    private RateCard rateCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_item_id", nullable = false)
    private CatalogItem catalogItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_uom_id", nullable = false)
    private Uom primaryUom;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "remarks", length = 255)
    private String remarks;

    public RateCard getRateCard() { return rateCard; }
    public void setRateCard(RateCard rateCard) { this.rateCard = rateCard; }

    public CatalogItem getCatalogItem() { return catalogItem; }
    public void setCatalogItem(CatalogItem catalogItem) { this.catalogItem = catalogItem; }

    public Uom getPrimaryUom() { return primaryUom; }
    public void setPrimaryUom(Uom primaryUom) { this.primaryUom = primaryUom; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
