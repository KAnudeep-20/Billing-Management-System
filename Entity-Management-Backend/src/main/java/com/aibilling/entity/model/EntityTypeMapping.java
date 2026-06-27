package com.aibilling.entity.model;

import com.aibilling.audit.BaseEntity;
import com.aibilling.setup.model.EntityType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * JPA entity mapping a core Entity to one of its Entity Types (roles).
 */
@Entity
@Table(name = "cx_entity_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private com.aibilling.entity.model.Entity entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_TYPE_ID", nullable = false)
    private EntityType entityType;

    public com.aibilling.entity.model.Entity getEntity() { return entity; }
    public void setEntity(com.aibilling.entity.model.Entity entity) { this.entity = entity; }
    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

}
