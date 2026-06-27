package com.aibilling.account.dto;

import com.aibilling.common.enums.Status;
import com.aibilling.setup.dto.LookupResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountResponse {

    private UUID id;
    private UUID entityId;
    private String accountName;
    private String natureOfBusiness;
    private Status status;
    private BigDecimal creditLimit;
    
    // Using LookupResponse to return ID + Name for linked setup data
    private LookupResponse paymentTerm;
    private LookupResponse billingCycle;

    private String creditClassification;
    private String creditRisk;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

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

    public LookupResponse getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(LookupResponse paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public LookupResponse getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(LookupResponse billingCycle) {
        this.billingCycle = billingCycle;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
