-- ============================================================
-- V3: Create Entity tables
-- ============================================================

-- 1. Insert additional Entity Types seeds
INSERT INTO entity_types (code, name, description, display_order) VALUES
('SUPPLIER', 'Supplier', 'External supplier entity', 25),
('INVESTOR', 'Investor', 'Investor entity', 60),
('OTHER', 'Other', 'Other entity type', 70);

-- 2. Create cx_entity table
CREATE TABLE cx_entity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_category VARCHAR(20) NOT NULL, -- 'ORGANIZATION', 'PERSON'
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    CONSTRAINT chk_entity_category CHECK (entity_category IN ('ORGANIZATION', 'PERSON'))
);

-- 3. Create cx_entity_type table (junction table mapping entity to its types/roles)
CREATE TABLE cx_entity_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_id UUID NOT NULL REFERENCES cx_entity(id) ON DELETE CASCADE,
    entity_type_id UUID NOT NULL REFERENCES entity_types(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX uq_cx_entity_type ON cx_entity_type(entity_id, entity_type_id) WHERE status != 'DELETED';

-- 4. Create cx_entity_details table (one-to-one extension of cx_entity)
CREATE TABLE cx_entity_details (
    entity_id UUID PRIMARY KEY REFERENCES cx_entity(id) ON DELETE CASCADE,
    organization_name VARCHAR(255),
    tin VARCHAR(50),
    full_name VARCHAR(255),
    identification_type VARCHAR(50),
    identification_number VARCHAR(100)
);
