# AI Billing System - Catalog Management Frontend Implementation Instructions

## Project Overview

This project is the frontend of the AI Billing System.

Current project structure:

```
Billing Management/
│
├── Entity-Management-Backend/
├── Entity-Management-Frontend/
├── Catalog Module.png
└── instructions.md
```

The Entity Management feature has already been completed in both backend and frontend.

The Catalog Management backend has also been completed.

Your responsibility is to implement **ONLY the Catalog Management frontend** by extending the existing frontend architecture.

Do NOT redesign the application.

Do NOT introduce a new UI framework.

The Catalog module must look and behave like it has always been part of the application.

---

# Step 1 - Analyze the Existing Frontend (MANDATORY)

Before writing any code, analyze the entire frontend project.

Location:

```
Entity-Management-Frontend/
```

Understand:

* Folder structure
* Routing architecture
* Layout system
* Navigation
* Design system
* Component library
* Page templates
* Table components
* Form components
* Dialogs
* Drawers
* State management
* API service layer
* Hooks
* Error handling
* Notification system
* Loading states
* Reusable utilities
* Styling approach
* Theme configuration

The Catalog module MUST reuse the existing architecture.

Do not recreate components that already exist.

Reuse wherever possible.

---

# Step 2 - Analyze the Existing Backend (MANDATORY)

Analyze the Catalog backend inside:

```
Entity-Management-Backend/
```

Understand:

* REST APIs
* Request DTOs
* Response DTOs
* Validation rules
* Lookup APIs
* Search APIs
* Relationships
* Pagination
* Sorting
* Error responses

The backend is the implementation source of truth.

Never invent fields.

Never hardcode lookup values.

---

# Step 3 - Analyze the Business Architecture (MANDATORY)

Open:

```
Catalog Module.png
```

Study:

* Category hierarchy
* Catalog Item structure
* Item types
* Item behavior flags
* UOM mapping
* Warehouse management
* Inventory Balance
* Inventory Transactions
* Business workflow
* Validation rules

The UI must accurately represent this architecture.

---

# Step 4 - UI Design Principles

Follow exactly the same enterprise design language already established by the Entity Management module.

Maintain:

* Same spacing
* Same typography
* Same color palette
* Same shadows
* Same cards
* Same buttons
* Same tables
* Same dialogs
* Same navigation
* Same responsiveness

Users should not be able to distinguish between the Entity and Catalog modules based on UI quality or styling.

---

# Step 5 - Feature Implementation Order

Implement incrementally.

## Phase 1 - Catalog Categories

Create:

* Category Listing
* Category Search
* Category Details
* Create Category
* Edit Category

Support:

* Parent Category
* Active/Inactive Status
* Recursive hierarchy
* Tree visualization (if supported)

Only leaf categories should be selectable for item assignment.

---

## Phase 2 - Catalog Items

Implement:

* Item Listing
* Search
* Filters
* Item Details
* Create Item
* Edit Item

Support:

* Goods
* Services
* Software
* Licenses
* Warranty

Display item behavior flags clearly.

Use badges or chips.

---

## Phase 3 - UOM Management

Display:

Primary UOM

Additional UOMs

Conversion Factors

Support:

* Add
* Edit
* Delete

Validate only one Primary UOM.

---

## Phase 4 - Warehouse Management

Implement:

Warehouse List

Warehouse Details

Create Warehouse

Edit Warehouse

Status Management

---

## Phase 5 - Inventory Balance

Read-only view.

Display:

* Quantity On Hand
* Reserved Quantity
* Available Quantity

No manual editing.

Use backend APIs only.

---

## Phase 6 - Inventory Transactions

Implement:

Transaction History

Filters

Search

Transaction Details

Support transaction types:

* Purchase Receipt
* Sales Issue
* Purchase Return
* Sales Return
* Inventory Adjustment
* Reservation
* Transfer

Display transaction timeline using enterprise data tables.

---

# Step 6 - Navigation

Extend existing navigation.

Add:

Catalog Management

Inside it:

* Categories
* Catalog Items
* Warehouses
* Inventory
* Inventory Transactions

Do not alter existing Entity navigation.

---

# Step 7 - Component Reuse

Reuse existing components whenever possible.

Examples:

* PageHeader
* SearchToolbar
* DataTable
* StatusBadge
* FormSection
* Drawer
* Modal
* ConfirmationDialog
* Pagination
* LoadingSkeleton
* EmptyState
* ErrorState
* LookupSelect

Only create new components when absolutely necessary.

---

# Step 8 - API Integration

Integrate every backend endpoint.

No mock data.

No static JSON.

All dropdowns must load dynamically from lookup APIs.

Follow existing API service architecture.

---

# Step 9 - Forms

Organize forms into logical sections.

Examples:

Catalog Item

Section 1

Basic Information

Section 2

Pricing

Section 3

Behavior

Section 4

Inventory

Section 5

UOM

Avoid long scrolling forms.

Prefer:

Drawers

Dialogs

Multi-section layouts

Reuse existing form components.

---

# Step 10 - Data Tables

All listing screens should follow the existing enterprise table design.

Support:

* Sticky headers
* Search
* Sorting
* Pagination
* Row selection
* Inline actions
* Status badges
* Loading skeletons
* Empty state

Maintain consistency with Entity Management tables.

---

# Step 11 - State Management

Separate:

* UI State
* Form State
* API State
* Lookup State
* Selection State

Reuse existing state management strategy.

---

# Step 12 - Validation

Follow backend validation.

Implement:

* Required fields
* Inline validation
* Business rule validation
* User-friendly error messages

Surface backend validation errors clearly.

---

# Step 13 - Performance

Implement:

* Lazy loading
* Code splitting
* Memoization where useful
* Efficient rendering
* API caching (if existing architecture supports it)

---

# Step 14 - Accessibility

Support:

* Keyboard navigation
* Proper labels
* Focus management
* Accessible dialogs
* Accessible tables

---

# Step 15 - Deliverables

The Catalog Management frontend is complete only when:

✓ Existing frontend architecture preserved

✓ Existing UI reused

✓ Backend fully integrated

✓ Categories completed

✓ Catalog Items completed

✓ UOM management completed

✓ Warehouses completed

✓ Inventory Balance completed

✓ Inventory Transactions completed

✓ Search completed

✓ Validation completed

✓ Responsive design completed

✓ Enterprise styling maintained

✓ No regressions in Entity Management

Do not implement Order Management or any future modules.
