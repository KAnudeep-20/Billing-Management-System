-- ============================================================
-- V10: Create Catalog Management Tables
-- ============================================================

-- 1. UOM (Unit of Measure) Table
CREATE TABLE uoms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_uoms_code ON uoms(code) WHERE status != 'DELETED';

-- 2. Catalog Categories Table
CREATE TABLE catalog_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    parent_category_id UUID REFERENCES catalog_categories(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_catalog_categories_code ON catalog_categories(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_catalog_categories_name ON catalog_categories(name) WHERE status != 'DELETED';
CREATE INDEX idx_catalog_categories_parent ON catalog_categories(parent_category_id);

-- 3. Catalog Items Table
CREATE TABLE catalog_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_number VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    category_id UUID NOT NULL REFERENCES catalog_categories(id),
    primary_uom_id UUID NOT NULL REFERENCES uoms(id),
    list_price DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    is_stocked BOOLEAN NOT NULL DEFAULT FALSE,
    is_inventory_tracked BOOLEAN NOT NULL DEFAULT FALSE,
    is_service BOOLEAN NOT NULL DEFAULT FALSE,
    is_sellable BOOLEAN NOT NULL DEFAULT TRUE,
    is_purchasable BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_catalog_items_number ON catalog_items(item_number) WHERE status != 'DELETED';
CREATE INDEX idx_catalog_items_category ON catalog_items(category_id);
CREATE INDEX idx_catalog_items_primary_uom ON catalog_items(primary_uom_id);

-- 4. Item UOM Table (Junction for Secondary UOMs)
CREATE TABLE item_uoms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES catalog_items(id) ON DELETE CASCADE,
    uom_id UUID NOT NULL REFERENCES uoms(id),
    conversion_factor DECIMAL(19, 6) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_item_uoms ON item_uoms(item_id, uom_id) WHERE status != 'DELETED';
CREATE INDEX idx_item_uoms_item ON item_uoms(item_id);
CREATE INDEX idx_item_uoms_uom ON item_uoms(uom_id);

-- 5. Warehouses Table
CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_warehouses_code ON warehouses(code) WHERE status != 'DELETED';

-- 6. Inventory Balances Table
CREATE TABLE inventory_balances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES catalog_items(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    quantity_on_hand DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    reserved_qty DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    available_qty DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX uq_inventory_balances ON inventory_balances(item_id, warehouse_id) WHERE status != 'DELETED';
CREATE INDEX idx_inventory_balances_item ON inventory_balances(item_id);
CREATE INDEX idx_inventory_balances_warehouse ON inventory_balances(warehouse_id);

-- 7. Inventory Transactions Table
CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES catalog_items(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_type VARCHAR(50) NOT NULL,
    reference_type VARCHAR(100),
    reference_id UUID,
    quantity DECIMAL(19, 4) NOT NULL,
    uom_id UUID NOT NULL REFERENCES uoms(id),
    conversion_factor DECIMAL(19, 6) NOT NULL,
    quantity_in_primary_uom DECIMAL(19, 4) NOT NULL,
    remarks VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_inventory_transactions_item ON inventory_transactions(item_id);
CREATE INDEX idx_inventory_transactions_warehouse ON inventory_transactions(warehouse_id);
CREATE INDEX idx_inventory_transactions_date ON inventory_transactions(transaction_date);

-- ============================================================
-- Seed Catalog Master Data
-- ============================================================

-- Seed UOMs
INSERT INTO uoms (code, description) VALUES
('EACH', 'Individual item unit'),
('HOUR', 'Service unit of time'),
('KG', 'Kilogram unit of weight'),
('LITER', 'Liter unit of volume'),
('BOX', 'Box unit container');

-- Seed Categories (Roots)
INSERT INTO catalog_categories (code, name, description) VALUES
('HARDWARE', 'Hardware', 'Physical computer systems and accessories'),
('SOFTWARE', 'Software', 'Digital software systems and licenses'),
('SERVICES', 'Services', 'Consulting and support services');

-- Seed Sub-Categories (using parent references)
INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'LAPTOPS', 'Laptops', 'Portable computing devices', id FROM catalog_categories WHERE code = 'HARDWARE';

INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'MONITORS', 'Monitors', 'Display output devices', id FROM catalog_categories WHERE code = 'HARDWARE';

INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'LICENSES', 'Licenses', 'Software execution rights', id FROM catalog_categories WHERE code = 'SOFTWARE';

INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'SUBSCRIPTIONS', 'Subscriptions', 'SaaS subscription services', id FROM catalog_categories WHERE code = 'SOFTWARE';

INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'CONSULTING', 'Consulting', 'Professional business consulting', id FROM catalog_categories WHERE code = 'SERVICES';

INSERT INTO catalog_categories (code, name, description, parent_category_id)
SELECT 'SUPPORT', 'Technical Support', 'Helpdesk and system support', id FROM catalog_categories WHERE code = 'SERVICES';

-- Seed Warehouses
INSERT INTO warehouses (code, name, address) VALUES
('WH-001', 'Primary Warehouse', 'Building A, Industrial Park, Suite 100'),
('WH-002', 'Secondary Warehouse', 'Building B, Industrial Park, Suite 200');
