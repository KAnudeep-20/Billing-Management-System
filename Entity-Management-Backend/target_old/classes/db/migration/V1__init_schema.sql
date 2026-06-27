-- ============================================================
-- V1: Baseline Migration — Entity Management Backend
-- ============================================================
-- This migration establishes the Flyway schema history table
-- and verifies database connectivity to Supabase PostgreSQL.
--
-- Future domain modules will add migrations:
--   V2__create_entity_tables.sql
--   V3__create_account_tables.sql
--   V4__create_site_tables.sql
--   V5__create_contact_tables.sql
--   etc.
-- ============================================================

-- Enable UUID generation extension (required for UUID primary keys)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
