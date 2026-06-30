-- ============================================================
-- V11: Create Rate Card Management Tables
-- ============================================================

-- 1. Create rate_cards table
CREATE TABLE rate_cards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rate_card_code VARCHAR(50) NOT NULL,
    rate_card_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    effective_from TIMESTAMP WITHOUT TIME ZONE,
    effective_to TIMESTAMP WITHOUT TIME ZONE,
    active_flag BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_rate_cards_code ON rate_cards(rate_card_code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_rate_cards_name ON rate_cards(rate_card_name) WHERE status != 'DELETED';
CREATE INDEX idx_rate_cards_code ON rate_cards(rate_card_code);
CREATE INDEX idx_rate_cards_name ON rate_cards(rate_card_name);

-- 2. Create rate_card_items table
CREATE TABLE rate_card_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rate_card_id UUID NOT NULL REFERENCES rate_cards(id),
    catalog_item_id UUID NOT NULL REFERENCES catalog_items(id),
    primary_uom_id UUID NOT NULL REFERENCES uoms(id),
    unit_price DECIMAL(19, 4) NOT NULL,
    remarks VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_rate_card_items_catalog ON rate_card_items(rate_card_id, catalog_item_id) WHERE status != 'DELETED';
CREATE INDEX idx_rate_card_items_card ON rate_card_items(rate_card_id);
CREATE INDEX idx_rate_card_items_catalog_item ON rate_card_items(catalog_item_id);

-- 3. Create entity_rate_cards table (Association table)
CREATE TABLE entity_rate_cards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_id UUID NOT NULL REFERENCES cx_entity(id) ON DELETE CASCADE,
    rate_card_id UUID NOT NULL REFERENCES rate_cards(id),
    effective_from TIMESTAMP WITHOUT TIME ZONE,
    effective_to TIMESTAMP WITHOUT TIME ZONE,
    active_flag BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_entity_rate_cards_active ON entity_rate_cards(entity_id) WHERE active_flag = TRUE AND status != 'DELETED';
CREATE INDEX idx_entity_rate_cards_entity ON entity_rate_cards(entity_id);
CREATE INDEX idx_entity_rate_cards_card ON entity_rate_cards(rate_card_id);
