-- ============================================================
-- V5: Create Site Tables
-- ============================================================

CREATE TABLE cx_cust_acct_site (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL REFERENCES cx_entity_accounts(id) ON DELETE CASCADE,
    site_name VARCHAR(255) NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    address_line_3 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(50),
    country VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE INDEX idx_cx_cust_acct_site_account_id ON cx_cust_acct_site(account_id);

CREATE TABLE cx_cust_acct_site_use (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    site_id UUID NOT NULL REFERENCES cx_cust_acct_site(id) ON DELETE CASCADE,
    site_use_id UUID NOT NULL REFERENCES site_uses(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX uq_cx_cust_acct_site_use ON cx_cust_acct_site_use(site_id, site_use_id) WHERE status != 'DELETED';
