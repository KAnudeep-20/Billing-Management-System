-- ============================================================
-- V4: Create Account Tables
-- ============================================================

CREATE TABLE cx_entity_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_id UUID NOT NULL REFERENCES cx_entity(id) ON DELETE CASCADE,
    account_name VARCHAR(255) NOT NULL,
    nature_of_business VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    credit_limit DECIMAL(19, 2),
    payment_term_id UUID REFERENCES payment_terms(id),
    billing_cycle_id UUID REFERENCES billing_cycles(id),
    credit_classification VARCHAR(100),
    credit_risk VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_cx_entity_accounts_entity_id ON cx_entity_accounts(entity_id);
