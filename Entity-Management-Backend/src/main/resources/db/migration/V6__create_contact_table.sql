-- ============================================================
-- V6: Create Contact Table
-- ============================================================

CREATE TABLE cx_cust_acct_contacts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL REFERENCES cx_entity_accounts(id) ON DELETE CASCADE,
    contact_type_id UUID REFERENCES contact_types(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(50),
    role VARCHAR(100),
    designation VARCHAR(100),
    is_placeholder BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_cx_cust_acct_contacts_account_id ON cx_cust_acct_contacts(account_id);
