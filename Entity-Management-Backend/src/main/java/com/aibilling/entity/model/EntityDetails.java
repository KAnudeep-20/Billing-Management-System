package com.aibilling.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA entity representing specific details of an Entity (Organization or Person).
 * Shares primary key with the parent Entity using MapsId.
 */
@Entity
@Table(name = "cx_entity_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDetails {

    @Id
    @Column(name = "entity_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "entity_id")
    private com.aibilling.entity.model.Entity entity;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Column(name = "tin", length = 50)
    private String tin;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "IDENTIFICATION_TYPE")
    private String identificationType;

    @Column(name = "IDENTIFICATION_NUMBER")
    private String identificationNumber;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public com.aibilling.entity.model.Entity getEntity() { return entity; }
    public void setEntity(com.aibilling.entity.model.Entity entity) { this.entity = entity; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getTin() { return tin; }
    public void setTin(String tin) { this.tin = tin; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String identificationType) { this.identificationType = identificationType; }
    public String getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(String identificationNumber) { this.identificationNumber = identificationNumber; }

}
