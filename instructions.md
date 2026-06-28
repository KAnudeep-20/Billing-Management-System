# AI Billing System - Catalog Management Backend Implementation Instructions

## Project Overview

You are extending an existing enterprise application called **AI Billing System**.

Current project directory structure:

Billing Management/
│
├── Entity-Management-Backend/
├── Entity-Management-Frontend/
├── Catalog Module.png
└── (this instructions.md)

The Entity Management feature has already been fully implemented in both backend and frontend.

Your responsibility is to implement **ONLY the backend** for the next feature:

# Catalog Management

Do NOT modify the existing Entity Management functionality unless integration is required.

---

# Step 1 - Understand the Existing Project (Mandatory)

Before writing any code, thoroughly analyze the existing backend project.

Location:

Entity-Management-Backend/

Understand:

- Overall project architecture
- Package structure
- Layered architecture
- Coding conventions
- Naming conventions
- Entity structure
- DTO pattern
- Repository pattern
- Service layer
- Controller design
- Exception handling
- Validation strategy
- API response format
- Audit implementation
- Logging
- Soft delete implementation
- Flyway migration strategy
- Lookup implementation
- Existing utility classes
- Existing configuration
- Existing security implementation (if present)

The Catalog Management module MUST follow exactly the same architecture and coding style.

Do not introduce a new architecture.

---

# Step 2 - Analyze the Catalog Module Architecture (Mandatory)

Open and study:

Catalog Module.png

located inside:

Billing Management/

Do not begin implementation until the entire architecture has been understood.

Analyze:

- Domain model
- Entity relationships
- Business workflow
- Inventory workflow
- Category hierarchy
- UOM management
- Warehouse management
- Inventory transactions
- Business rules
- Lookup data
- Master data dependencies

The image is the business source of truth.

Implement according to it.

---

# Step 3 - Understand Module Scope

The Catalog Management module is NOT simply Product Management.

It is an enterprise master-data module responsible for:

• Catalog Categories
• Catalog Items
• Item UOMs
• Warehouses
• Inventory Balances
• Inventory Transactions

This module will become the foundation for:

Order Management

↓

Transaction Management

↓

Invoice Management

↓

AI Insights

↓

Predictive Cash Flow

Design accordingly.

---

# Step 4 - Preserve Existing Project Standards

Everything added must follow the existing backend implementation.

Reuse:

- Base entities
- Base repositories
- Base services
- Common DTOs
- Generic response wrappers
- Exception handlers
- Validation annotations
- Audit framework
- Lookup architecture

Never duplicate functionality that already exists.

---

# Step 5 - Backend Implementation Order

Implement incrementally.

Phase 1

Catalog Category

- Database
- Entity
- Repository
- DTOs
- Mapper
- Service
- Controller
- Validation
- Flyway Migration

Business Rules

- Parent Category support
- Recursive hierarchy
- Active/Inactive
- Only leaf categories can contain catalog items

---

Phase 2

Catalog Item

Implement

- Entity
- DTOs
- CRUD APIs
- Validation
- Mapping
- Relationships

Support

- Goods
- Services
- Software
- Licenses
- Warranty

Item Behaviour Flags

- Sellable
- Purchasable
- Inventory Tracked
- Stocked
- Service Item

---

Phase 3

Item UOM

Implement

- UOM Mapping
- Primary UOM
- Conversion Factors

Rules

- One Primary UOM
- Multiple Secondary UOMs
- Conversion validation

---

Phase 4

Warehouse

Implement

Warehouse Master

Support

- CRUD
- Status
- Address
- Validation

---

Phase 5

Inventory Balance

Implement inventory balance model.

Inventory Balance should NOT be manually edited.

It must always represent the calculated stock.

---

Phase 6

Inventory Transactions

Implement inventory ledger.

Support transaction types such as:

- Purchase Receipt
- Sales Issue
- Purchase Return
- Sales Return
- Inventory Adjustment
- Transfer In
- Transfer Out
- Reservation
- Reservation Release

Inventory Balance must be updated through transactions.

Never update stock directly.

---

# Step 6 - Database

Follow existing Flyway migration strategy.

Create normalized tables.

Follow naming conventions already used in Entity Management.

Maintain proper foreign keys.

Maintain indexes where required.

Do not duplicate lookup tables if reusable ones already exist.

---

# Step 7 - REST APIs

Design REST endpoints consistent with the Entity Management module.

Support:

Categories

- Create
- Update
- Delete
- Search
- Get Details

Catalog Items

- CRUD
- Search

Warehouses

- CRUD

UOM

- CRUD

Inventory Transactions

- Create
- Search
- History

Inventory Balance

- View

Maintain existing response structure.

---

# Step 8 - Validation

Implement all business rules.

Examples:

- Category hierarchy validation
- Leaf category validation
- Primary UOM validation
- Warehouse validation
- Inventory quantity validation
- Duplicate code prevention
- Required fields
- Status validation

Validation should exist in both DTOs and Service layer where appropriate.

---

# Step 9 - Exception Handling

Reuse existing exception architecture.

Never return raw exceptions.

Return standardized API responses.

---

# Step 10 - Logging & Auditing

Every create, update, delete operation must follow the same audit mechanism already implemented in Entity Management.

Do not introduce a separate audit system.

---

# Step 11 - Code Quality

Follow:

- SOLID principles
- Clean Architecture
- Layer separation
- Reusable services
- Constructor injection
- No duplicated logic
- Clear package organization
- Meaningful class names

---

# Step 12 - Integration

The new module must integrate cleanly into the existing backend.

Do not break Entity Management.

Reuse existing infrastructure whenever possible.

Catalog Management should feel like it has always been part of the project.

---

# Expected Deliverables

The task is complete only when:

✓ Catalog Categories implemented

✓ Catalog Items implemented

✓ Item UOM implemented

✓ Warehouses implemented

✓ Inventory Balance implemented

✓ Inventory Transactions implemented

✓ Flyway migrations added

✓ REST APIs completed

✓ Validation completed

✓ Logging integrated

✓ Auditing integrated

✓ Existing architecture preserved

✓ Backend compiles successfully

Do not begin frontend implementation.

Only implement the backend for Catalog Management.