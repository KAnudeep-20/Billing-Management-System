package com.aibilling.entity.model;

import com.aibilling.audit.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a Core Entity (Organization or Person).
 * Acts as the aggregate root for Entity Management.
 */
@jakarta.persistence.Entity
@Table(name = "cx_entity")
@SQLDelete(sql = "UPDATE cx_entity SET status = 'DELETED' WHERE id = ? and version = ?")
@SQLRestriction("status != 'DELETED'")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_category", nullable = false, length = 20)
    private EntityCategory entityCategory;

    @OneToOne(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EntityDetails details;

    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EntityTypeMapping> entityTypes = new HashSet<>();

    public EntityCategory getEntityCategory() { return entityCategory; }
    public void setEntityCategory(EntityCategory entityCategory) { this.entityCategory = entityCategory; }
    public EntityDetails getDetails() { return details; }
    public void setDetails(EntityDetails details) { 
        if (details == null) {
            if (this.details != null) {
                this.details.setEntity(null);
            }
        } else {
            details.setEntity(this);
        }
        this.details = details; 
    }
    public Set<EntityTypeMapping> getEntityTypes() { return entityTypes; }
    public void setEntityTypes(Set<EntityTypeMapping> entityTypes) { this.entityTypes = entityTypes; }

    /**
     * Helper method to add an entity type mapping and establish bidirectional reference.
     */
    public void addEntityType(EntityTypeMapping mapping) {
        if (this.entityTypes == null) {
            this.entityTypes = new HashSet<>();
        }
        this.entityTypes.add(mapping);
        mapping.setEntity(this);
    }
}
