-- ============================================================
-- V7: Create Entity Relationship Tables
-- ============================================================

-- Drop the old relationship_types table if it exists
DROP TABLE IF EXISTS relationship_types CASCADE;

-- Create cx_entity_rel_types table
CREATE TABLE cx_entity_rel_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX uq_cx_entity_rel_types_code ON cx_entity_rel_types(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_cx_entity_rel_types_name ON cx_entity_rel_types(name) WHERE status != 'DELETED';

-- Seed cx_entity_rel_types with requested relationship types
INSERT INTO cx_entity_rel_types (code, name, description, display_order) VALUES
('PARENT_OF', 'Parent Of', 'Subject entity is parent of object entity', 10),
('SUBSIDIARY_OF', 'Subsidiary Of', 'Subject entity is subsidiary of object entity', 20),
('DISTRIBUTOR_OF', 'Distributor Of', 'Subject entity is distributor of object entity', 30),
('FRANCHISE_OF', 'Franchise Of', 'Subject entity is franchise of object entity', 40),
('PARTNER_OF', 'Partner Of', 'Subject entity is partner of object entity', 50);

-- Create cx_entity_relationships table
CREATE TABLE cx_entity_relationships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_entity_id UUID NOT NULL,
    relationship_type_id UUID NOT NULL,
    object_entity_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    CONSTRAINT fk_relationship_subject FOREIGN KEY (subject_entity_id) REFERENCES cx_entity (id),
    CONSTRAINT fk_relationship_type FOREIGN KEY (relationship_type_id) REFERENCES cx_entity_rel_types (id),
    CONSTRAINT fk_relationship_object FOREIGN KEY (object_entity_id) REFERENCES cx_entity (id),
    CONSTRAINT chk_no_self_reference CHECK (subject_entity_id != object_entity_id)
);

CREATE UNIQUE INDEX uq_cx_entity_relationships ON cx_entity_relationships (subject_entity_id, relationship_type_id, object_entity_id) WHERE status != 'DELETED';
