package com.aibilling.ratecard.model;

import com.aibilling.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "rate_cards")
@SQLDelete(sql = "UPDATE rate_cards SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateCard extends BaseEntity {

    @Column(name = "rate_card_code", nullable = false, length = 50)
    private String rateCardCode;

    @Column(name = "rate_card_name", nullable = false, length = 100)
    private String rateCardName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "active_flag", nullable = false)
    private boolean activeFlag = true;

    public String getRateCardCode() { return rateCardCode; }
    public void setRateCardCode(String rateCardCode) { this.rateCardCode = rateCardCode; }

    public String getRateCardName() { return rateCardName; }
    public void setRateCardName(String rateCardName) { this.rateCardName = rateCardName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }

    public boolean isActiveFlag() { return activeFlag; }
    public void setActiveFlag(boolean activeFlag) { this.activeFlag = activeFlag; }
}
