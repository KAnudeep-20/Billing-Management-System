-- ============================================================
-- V2: Create Setup / Master Data Tables
-- ============================================================

-- 1. Payment Terms
CREATE TABLE payment_terms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    days_due INTEGER NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX uq_payment_terms_code ON payment_terms(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_payment_terms_name ON payment_terms(name) WHERE status != 'DELETED';

-- 2. Billing Cycles
CREATE TABLE billing_cycles (
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

CREATE UNIQUE INDEX uq_billing_cycles_code ON billing_cycles(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_billing_cycles_name ON billing_cycles(name) WHERE status != 'DELETED';

-- 3. Entity Types
CREATE TABLE entity_types (
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

CREATE UNIQUE INDEX uq_entity_types_code ON entity_types(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_entity_types_name ON entity_types(name) WHERE status != 'DELETED';

-- 4. Contact Types
CREATE TABLE contact_types (
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

CREATE UNIQUE INDEX uq_contact_types_code ON contact_types(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_contact_types_name ON contact_types(name) WHERE status != 'DELETED';

-- 5. Site Uses
CREATE TABLE site_uses (
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

CREATE UNIQUE INDEX uq_site_uses_code ON site_uses(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_site_uses_name ON site_uses(name) WHERE status != 'DELETED';

-- 6. Relationship Types
CREATE TABLE relationship_types (
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

CREATE UNIQUE INDEX uq_relationship_types_code ON relationship_types(code) WHERE status != 'DELETED';
CREATE UNIQUE INDEX uq_relationship_types_name ON relationship_types(name) WHERE status != 'DELETED';


-- ============================================================
-- Seed Data
-- ============================================================

-- Seed Payment Terms
INSERT INTO payment_terms (code, name, description, days_due, display_order) VALUES
('NET_15', 'NET 15', 'Payment due within 15 days of invoice date', 15, 10),
('NET_30', 'NET 30', 'Payment due within 30 days of invoice date', 30, 20),
('NET_45', 'NET 45', 'Payment due within 45 days of invoice date', 45, 30),
('NET_60', 'NET 60', 'Payment due within 60 days of invoice date', 60, 40);

-- Seed Billing Cycles
INSERT INTO billing_cycles (code, name, description, display_order) VALUES
('MONTHLY', 'Monthly', 'Billed once per month', 10),
('QUARTERLY', 'Quarterly', 'Billed once per quarter (every 3 months)', 20),
('YEARLY', 'Yearly', 'Billed once per year', 30),
('CUSTOM', 'Custom', 'Billed on custom schedule', 40);

-- Seed Entity Types
INSERT INTO entity_types (code, name, description, display_order) VALUES
('CUSTOMER', 'Customer', 'External customer entity', 10),
('VENDOR', 'Vendor', 'External vendor or supplier entity', 20),
('PARTNER', 'Partner', 'Strategic partner entity', 30),
('AFFILIATE', 'Affiliate', 'Corporate affiliate entity', 40),
('INTERNAL', 'Internal', 'Internal legal entity', 50);

-- Seed Contact Types
INSERT INTO contact_types (code, name, description, display_order) VALUES
('PRIMARY', 'Primary', 'Primary point of contact', 10),
('BILLING', 'Billing', 'Billing/Finance contact for invoices', 20),
('SHIPPING', 'Shipping', 'Logistics/Delivery contact', 30),
('TECHNICAL', 'Technical', 'IT or system administration contact', 40),
('ESCALATION', 'Escalation', 'Management/Escalation level contact', 50);

-- Seed Site Uses
INSERT INTO site_uses (code, name, description, display_order) VALUES
('BILL_TO', 'Bill To', 'Address used for invoicing and billing statements', 10),
('SHIP_TO', 'Ship To', 'Address used for shipping goods and physical delivery', 20),
('SOLD_TO', 'Sold To', 'Address associated with purchasing entity', 30),
('PRIMARY', 'Primary', 'Primary operational address for the entity', 40);

-- Seed Relationship Types
INSERT INTO relationship_types (code, name, description, display_order) VALUES
('PARENT_COMPANY', 'Parent Company', 'The parent entity of a subsidiary', 10),
('SUBSIDIARY', 'Subsidiary', 'A subsidiary owned/controlled by a parent', 20),
('AFFILIATE', 'Affiliate', 'An affiliated company under common control', 30),
('PARTNER', 'Partner', 'A strategic business partner', 40);
