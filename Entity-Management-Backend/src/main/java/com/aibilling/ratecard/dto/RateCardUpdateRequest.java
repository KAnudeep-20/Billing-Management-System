package com.aibilling.ratecard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCardUpdateRequest {

    @NotBlank(message = "Rate Card code is required")
    @Size(max = 50, message = "Rate Card code must not exceed 50 characters")
    private String rateCardCode;

    @NotBlank(message = "Rate Card name is required")
    @Size(max = 100, message = "Rate Card name must not exceed 100 characters")
    private String rateCardName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private boolean activeFlag;

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
