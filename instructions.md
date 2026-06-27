# Entity Management - Frontend, Backend & Database Audit Instructions

# Objective

Perform a complete audit of the Entity Management module across:

* Frontend
* Backend
* Database (Supabase)

The objective is to identify the root causes of the existing issues, fix them correctly, clean invalid data, and verify the entire Entity Management workflow.

Do not implement new features.

Only fix defects, clean invalid data, and ensure the existing implementation behaves exactly as intended by the Entity Management requirements.

---

# IMPORTANT

Use the connected **Supabase MCP** to inspect the database directly.

Do not assume the frontend is wrong.

Do not assume the backend is wrong.

Determine the actual root cause before making changes.

---

# Issue 1 - Search Results Showing "N/A"

## Problem

Searching for an entity correctly returns the matching record.

However, every search result is displayed as:

"N/A"

Clicking the result navigates to the correct Entity Details page.

This indicates that:

* search is finding the correct entity
* identifier is correct
* display field is incorrect

## Tasks

Inspect:

Frontend

* Search component
* Search dropdown
* Search result renderer
* Entity model
* API response mapping

Backend

* Search endpoint
* DTO mapping
* Projection
* Response serializer

Database

Verify that Entity Name is actually stored.

Determine why "N/A" is being rendered.

Fix the root cause.

Search results must display the real Entity Name.

---

# Issue 2 - Invalid Entity Records

Inspect every entity stored in Supabase.

Find all entities that violate the business rules.

Examples:

Entity without Account

Entity without Site

Entity without Contact

Invalid payment terms

Broken relationships

Orphaned records

Missing mandatory information

Any record violating the PRD should be considered invalid.

---

# Issue 3 - Clean Invalid Data

Delete every invalid entity.

Do not delete valid entities.

Respect foreign key constraints.

Delete dependent records correctly.

After cleanup:

Database should only contain valid entities.

---

# Issue 4 - Seed Correct Sample Data

Create realistic sample entities.

Use the application APIs if possible.

If necessary, insert directly into Supabase while maintaining referential integrity.

Create multiple examples for:

Customer

Supplier

Partner

Investor

Each sample entity must contain:

Entity

At least one Account

At least one Site

One Primary Site

At least one Contact

Valid Payment Terms

Credit Information

Entity Type

Relationship (where applicable)

The sample data should resemble production-quality business data.

---

# Issue 5 - "N/A" Entity Cannot Be Deleted

There is an entity displayed as:

"N/A"

Delete action does not work.

Investigate:

Frontend

Backend

Database

Possible causes include:

Soft delete issue

Foreign key constraint

Delete API bug

Entity ID mismatch

Orphaned records

Permission issue

Broken transaction

Determine the exact cause.

If the entity is invalid:

Delete it safely.

If it is valid:

Repair it so that it behaves normally.

The Delete button must work correctly.

---

# Issue 6 - Entity Details Consistency

Verify every Entity Details page.

Ensure:

Entity Summary

Accounts

Sites

Contacts

Relationships

Payment Terms

Credit Information

All load correctly.

No missing fields.

No placeholder values.

No "N/A" values unless genuinely absent.

---

# Backend Audit

Verify:

Search endpoint

Entity details endpoint

Delete endpoint

Entity retrieval

DTO mapping

MapStruct mappings

Null handling

Validation

Exception handling

Logging

No hidden exceptions.

---

# Frontend Audit

Verify:

Search component

API integration

React Query / Data fetching

Display mapping

Delete action

Cache invalidation

Entity Details rendering

Forms

Routing

Fix any incorrect property mappings.

---

# Database Audit

Using Supabase MCP:

Inspect:

Entities

Accounts

Sites

Contacts

Relationships

Payment Terms

Entity Types

Contact Types

Site Uses

Billing Cycles

Ensure:

No orphaned rows

No invalid references

No duplicate records

No broken foreign keys

Repair any inconsistencies.

---

# Validation

After completing all fixes, verify the following:

Search returns the correct entities.

Search displays actual Entity Names.

Entity Details page loads correctly.

Delete works.

No "N/A" entities remain unless valid.

Every entity satisfies:

* at least one Account
* at least one Site
* one Primary Site
* at least one Contact

Seeded entities appear correctly.

CRUD operations still function.

Create Entity flow still works without regression.

No frontend console errors.

No backend exceptions.

No database inconsistencies.

Finally perform an end-to-end verification:

Create Entity

Search Entity

Open Entity Details

Update Entity

Delete Entity

Repeat for multiple entity types.

Only declare the task complete after every verification succeeds.
