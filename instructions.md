# AI Billing System - Rate Card Management Backend Implementation Instructions

# Project Overview

You are extending an existing enterprise application called **AI Billing System**.

Current project structure:

Billing Management/
│
├── Entity-Management-Backend/
├── Entity-Management-Frontend/
├── Catalog Module.png
├── (Future Modules)
└── instructions.md

The following modules are already completed:

- Entity Management (Backend & Frontend)
- Catalog Management (Backend & Frontend)

Your responsibility is to implement **ONLY the backend** for the next feature:

# Rate Card Management

Do NOT modify the architecture of existing modules.

Extend the existing backend following the same architecture, coding standards, conventions, and design patterns.

---

# Step 1 - Analyze Existing Backend (MANDATORY)

Before writing any code, analyze the existing backend project.

Understand:

- Package structure
- Layered architecture
- Naming conventions
- Entity design
- DTO pattern
- Repository pattern
- Service layer
- Controller layer
- Validation strategy
- Exception handling
- Generic API responses
- Audit implementation
- Logging
- Soft delete
- Flyway migrations
- Lookup implementation

Rate Card Management must follow the exact same project architecture.

Do not introduce a new architecture.

---

# Step 2 - Analyze Existing Modules

Study the implementation of:

Entity Management

Understand:

- Payment Terms implementation
- Entity relationships
- Existing lookup usage
- Search implementation

Study:

Catalog Management

Understand:

- Catalog Item
- Primary UOM
- Item Status
- List Price
- Sellable flag
- Item lookup APIs

Rate Card must integrate with these modules.

---

# Step 3 - Module Purpose

Rate Card Management is an independent master module.

It defines the selling price of Catalog Items.

It does NOT own Catalog Items.

It references Catalog Items.

Rate Card will later be associated with Entity Management.

Relationship:

Catalog Item

↓

Rate Card Item

↓

Rate Card

↓

Entity

---

# Step 4 - Database Design

Create the following tables.

------------------------------------

RATE_CARD

------------------------------------

Fields

- RateCardId
- RateCardCode (Unique)
- RateCardName
- Description
- Currency
- Status
- EffectiveFrom
- EffectiveTo
- ActiveFlag
- Audit Fields
- Soft Delete Flag

Business Rules

- Code must be unique.
- Name must be unique.
- Effective dates are optional.
- Support Active / Inactive status.

------------------------------------

RATE_CARD_ITEM

------------------------------------

Fields

- RateCardItemId
- RateCardId (FK)
- CatalogItemId (FK)
- PrimaryUomId (FK)
- UnitPrice
- Remarks
- Status
- Audit Fields
- Soft Delete Flag

Business Rules

- One Catalog Item can appear only once in a Rate Card.
- Primary UOM must be copied automatically from Catalog.
- User cannot modify Primary UOM.
- Unit Price must be greater than zero.
- Catalog Item must exist.
- Catalog Item must be active.
- Catalog Item must be sellable.

------------------------------------

ENTITY_RATE_CARD

------------------------------------

Association table.

Fields

- EntityRateCardId
- EntityId (FK)
- RateCardId (FK)
- EffectiveFrom
- EffectiveTo
- ActiveFlag
- Audit Fields

Business Rules

- Only one active Rate Card per Entity.
- Historical assignments should be retained.
- Entity stores only the association.

---

# Step 5 - Domain Entities

Create:

RateCard

RateCardItem

EntityRateCard

Reuse:

- BaseEntity
- AuditEntity
- Soft Delete
- Existing lookup strategy

---

# Step 6 - DTO Layer

Create DTOs.

Rate Card

- Create
- Update
- Response
- Search

Rate Card Item

- Add Item
- Update Price
- Response

Entity Assignment

- Assign Rate Card
- Update Assignment
- Response

Follow existing DTO conventions.

---

# Step 7 - Repository Layer

Repositories

RateCardRepository

RateCardItemRepository

EntityRateCardRepository

Support:

- Search by Code
- Search by Name
- Search by Status
- Get Active Rate Cards
- Get Items by Rate Card
- Get Assigned Rate Card for Entity

Reuse existing repository patterns.

---

# Step 8 - Service Layer

Implement:

Rate Card Service

Responsibilities

- Create
- Update
- Delete
- Activate
- Deactivate
- Search
- Get Details

--------------------------------------------------

Rate Card Item Service

Responsibilities

- Add Item
- Update Price
- Remove Item
- Get Items

Business Logic

When user selects Catalog Item

↓

Fetch Catalog Item

↓

Automatically fetch Primary UOM

↓

Populate Primary UOM

↓

Save Unit Price

Do NOT allow manual Primary UOM editing.

Keep List Price untouched.

List Price is only a reference value.

Rate Card Price becomes the operational selling price.

--------------------------------------------------

Entity Rate Card Service

Responsibilities

- Assign Rate Card
- Remove Assignment
- Replace Assignment
- Get Assigned Rate Card

Business Rule

Deactivate previous assignment before activating new assignment.

---

# Step 9 - Controller Layer

Create REST APIs.

Rate Card

- Create
- Update
- Delete
- Search
- Get Details

Rate Card Item

- Add Item
- Update Price
- Remove Item
- List Items

Entity Assignment

- Assign
- Replace
- Remove
- Get Assigned Rate Card

Maintain existing response format.

---

# Step 10 - Validation

Implement validation.

Examples

- Unique Rate Card Code
- Unique Rate Card Name
- Positive Price
- Duplicate Catalog Item prevention
- Active Catalog Item
- Sellable Catalog Item
- Primary UOM exists
- Effective dates validation

Reuse existing validation framework.

---

# Step 11 - Flyway Migration

Create Flyway migrations.

Follow naming conventions.

Maintain:

- Foreign Keys
- Indexes
- Constraints

Reuse existing migration strategy.

---

# Step 12 - Audit & Logging

Reuse:

- Existing Audit Framework
- Logging
- Exception Handling
- Soft Delete

No duplicate implementations.

---

# Step 13 - Search APIs

Support searching by:

Rate Cards

- Code
- Name
- Status

Rate Card Items

- Item Code
- Item Name

Entity Assignment

- Entity Name
- Rate Card Name

Support pagination and sorting.

---

# Step 14 - Integration

Catalog Integration

Read only

Reuse:

- Catalog Item
- Primary UOM
- Item Status
- Sellable flag

Do not duplicate catalog information.

--------------------------------------------------

Entity Integration

Add Rate Card association to Entity.

Exactly like Payment Terms.

Do not embed Rate Card inside Entity.

Entity stores only RateCardId.

---

# Step 15 - Future Readiness

Expose reusable service methods for future modules.

Examples

Get Price By Rate Card

Get Price By Catalog Item

Get Price By Entity

These APIs will later be used by Order Management.

---

# Completion Checklist

The backend is complete only when:

✓ Rate Card Master implemented

✓ Rate Card Item implemented

✓ Entity association implemented

✓ Database design completed

✓ Flyway migrations completed

✓ CRUD APIs completed

✓ Search APIs completed

✓ Validation completed

✓ Audit integrated

✓ Logging integrated

✓ Existing architecture preserved

✓ Backend builds successfully

Do not implement frontend.

Only complete the backend.