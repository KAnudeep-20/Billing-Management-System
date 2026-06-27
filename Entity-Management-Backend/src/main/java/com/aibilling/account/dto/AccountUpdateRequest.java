package com.aibilling.account.dto;

import com.aibilling.common.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountUpdateRequest {

    @NotBlank(message = "Account name is required")
    @Size(max = 255, message = "Account name cannot exceed 255 characters")
    private String accountName;

    @Size(max = 255, message = "Nature of business cannot exceed 255 characters")
    private String natureOfBusiness;

    @NotNull(message = "Status is required")
    private Status status;

    private BigDecimal creditLimit;

    private UUID paymentTermId;

    private UUID billingCycleId;

    @Size(max = 100, message = "Credit classification cannot exceed 100 characters")
    private String creditClassification;

    @Size(max = 100, message = "Credit risk cannot exceed 100 characters")
    private String creditRisk;

    // Getters and Setters

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public void setNatureOfBusiness(String natureOfBusiness) {
        this.natureOfBusiness = natureOfBusiness;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public UUID getPaymentTermId() {
        return paymentTermId;
    }

    public void setPaymentTermId(UUID paymentTermId) {
        this.paymentTermId = paymentTermId;
    }

    public UUID getBillingCycleId() {
        return billingCycleId;
    }

    public void setBillingCycleId(UUID billingCycleId) {
        this.billingCycleId = billingCycleId;
    }

    public String getCreditClassification() {
        return creditClassification;
    }

    public void setCreditClassification(String creditClassification) {
        this.creditClassification = creditClassification;
    }

    public String getCreditRisk() {
        return creditRisk;
    }

    public void setCreditRisk(String creditRisk) {
        this.creditRisk = creditRisk;
    }
}
