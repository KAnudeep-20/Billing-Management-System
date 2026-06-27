-- Create indexes to optimize global entity search

CREATE INDEX idx_entity_details_org_name ON cx_entity_details(organization_name);
CREATE INDEX idx_entity_details_full_name ON cx_entity_details(full_name);
CREATE INDEX idx_account_name ON cx_entity_accounts(account_name);
CREATE INDEX idx_contact_first_name ON cx_cust_acct_contacts(first_name);
CREATE INDEX idx_contact_last_name ON cx_cust_acct_contacts(last_name);
