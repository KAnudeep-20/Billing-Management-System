import React, { useState, useEffect, useCallback } from 'react';
import MainLayout from './layouts/MainLayout';
import SearchToolbar from './features/entity-management/components/SearchToolbar';
import EntityTable from './features/entity-management/components/EntityTable';
import EntitySummaryCard from './features/entity-management/components/EntitySummaryCard';
import AccountTable from './features/entity-management/components/AccountTable';
import SiteTable from './features/entity-management/components/SiteTable';
import ContactTable from './features/entity-management/components/ContactTable';
import RelationshipSection from './features/entity-management/components/RelationshipSection';
import MultiStepForm from './features/entity-management/components/MultiStepForm';
import AccountFormModal from './features/entity-management/components/AccountFormModal';
import SiteFormModal from './features/entity-management/components/SiteFormModal';
import ContactFormModal from './features/entity-management/components/ContactFormModal';
import useLookupData from './features/entity-management/hooks/useLookupData';
import entityService from './services/entityService';
import './features/entity-management/EntityManagement.css';
import { ShieldAlert, Info, AlertTriangle, ArrowLeft } from 'lucide-react';

export default function App() {
  // Global lookups data
  const { lookups, loading: lookupsLoading, error: lookupsError } = useLookupData();

  // Search & List State
  const [entities, setEntities] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [listLoading, setListLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Selection & Details State
  const [selectedEntityId, setSelectedEntityId] = useState(null);
  const [selectedEntityDetails, setSelectedEntityDetails] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [selectedAccountId, setSelectedAccountId] = useState(null);
  const [selectedAccount, setSelectedAccount] = useState(null);

  // Form Wizard Modals State
  const [isWizardOpen, setIsWizardOpen] = useState(false);
  const [editingEntity, setEditingEntity] = useState(null);

  // Child Modals State (Accounts, Sites, Contacts)
  const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
  const [editingAccount, setEditingAccount] = useState(null);

  const [isSiteModalOpen, setIsSiteModalOpen] = useState(false);
  const [editingSite, setEditingSite] = useState(null);

  const [isContactModalOpen, setIsContactModalOpen] = useState(false);
  const [editingContact, setEditingContact] = useState(null);

  // Notifications State
  const [notification, setNotification] = useState(null);

  // Helper to trigger toast notifications
  const triggerNotification = (type, message) => {
    setNotification({ type, message });
    setTimeout(() => {
      setNotification(null);
    }, 4500);
  };

  // --- API OPERATIONS ---

  // Fetch Entities (Standard pagination or search)
  const fetchEntities = useCallback(async (queryVal = '', pageNum = 0) => {
    setListLoading(true);
    try {
      let res;
      if (queryVal.trim()) {
        res = await entityService.searchEntities(queryVal, pageNum, 10);
        // Search controller returns Spring Page object directly
        setEntities(res.content || []);
        setTotalPages(res.totalPages || 0);
        setTotalElements(res.totalElements || 0);
      } else {
        res = await entityService.getEntities(pageNum, 10);
        // Entity controller returns ApiResponse wrap with PageResponse
        const pageData = res.data || {};
        setEntities(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
        setTotalElements(pageData.totalElements || 0);
      }
      setPage(pageNum);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to retrieve entity list.');
    } finally {
      setListLoading(false);
    }
  }, []);

  // Fetch complete details hierarchy of selected entity
  const fetchEntityDetails = async (id) => {
    setDetailsLoading(true);
    try {
      const res = await entityService.getCompleteEntityDetails(id);
      const details = res.data || null;
      setSelectedEntityDetails(details);

      // Auto-select first account on details load, matching UX specification
      if (details && details.accounts && details.accounts.length > 0) {
        const firstAccount = details.accounts[0];
        setSelectedAccountId(firstAccount.id);
        setSelectedAccount(firstAccount);
      } else {
        setSelectedAccountId(null);
        setSelectedAccount(null);
      }
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to fetch customer details.');
    } finally {
      setDetailsLoading(false);
    }
  };

  // Run on mount and search changes
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchEntities(searchQuery, 0);
    }, 400); // debounce input typing
    return () => clearTimeout(timer);
  }, [searchQuery, fetchEntities]);

  // Handle entity row click
  const handleEntityRowClick = (entity) => {
    setSelectedEntityId(entity.id);
    fetchEntityDetails(entity.id);
  };

  // Handle Account Selection (updates Section C & D details)
  const handleAccountSelect = (acct) => {
    setSelectedAccountId(acct.id);
    setSelectedAccount(acct);
  };

  // Soft delete entity handler
  const handleDeleteEntity = async (entity) => {
    if (window.confirm(`Are you sure you want to delete customer entity "${entity.entityCategory === 'ORGANIZATION' ? (entity.organizationDetails?.organizationName || entity.organizationName) : (entity.personDetails?.fullName || entity.fullName)}"?`)) {
      try {
        await entityService.deleteEntity(entity.id);
        triggerNotification('success', 'Customer entity deleted successfully.');
        fetchEntities(searchQuery, page);
        if (selectedEntityId === entity.id) {
          setSelectedEntityId(null);
          setSelectedEntityDetails(null);
        }
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete customer entity.');
      }
    }
  };

  // Handle Wizard Submission (Create or Edit Entity)
  const handleWizardSubmit = async (formData, editId) => {
    if (editId) {
      // Edit mode: only update entity attributes
      const payload = {
        entityCategory: formData.entityCategory,
        entityTypeCodes: formData.entityTypeCodes,
        status: formData.status || 'ACTIVE',
        organizationDetails: formData.entityCategory === 'ORGANIZATION' ? {
          organizationName: formData.organizationName,
          tin: formData.tin
        } : null,
        personDetails: formData.entityCategory === 'PERSON' ? {
          fullName: formData.fullName,
          identificationType: formData.identificationType,
          identificationNumber: formData.identificationNumber
        } : null
      };

      await entityService.updateEntity(editId, payload);
      triggerNotification('success', 'Entity profile updated successfully.');
      fetchEntities(searchQuery, page);
      if (selectedEntityId === editId) {
        fetchEntityDetails(editId);
      }
    } else {
      // Create mode: Orchestrate sequential saves to build hierarchy
      // 1. Create Entity
      const entityPayload = {
        entityCategory: formData.entityCategory,
        entityTypeCodes: formData.entityTypeCodes,
        organizationDetails: formData.entityCategory === 'ORGANIZATION' ? {
          organizationName: formData.organizationName,
          tin: formData.tin
        } : null,
        personDetails: formData.entityCategory === 'PERSON' ? {
          fullName: formData.fullName,
          identificationType: formData.identificationType,
          identificationNumber: formData.identificationNumber
        } : null
      };

      const entityRes = await entityService.createEntity(entityPayload);
      const newEntityId = entityRes.data.id;

      // 2. Create Account (linked to newEntityId)
      const accountPayload = {
        entityId: newEntityId,
        accountName: formData.accountName,
        natureOfBusiness: formData.natureOfBusiness,
        creditLimit: formData.creditLimit ? parseFloat(formData.creditLimit) : null,
        paymentTermId: formData.paymentTermId || null,
        billingCycleId: formData.billingCycleId || null,
        creditClassification: formData.creditClassification || null,
        creditRisk: formData.creditRisk || null
      };

      const accountRes = await entityService.createAccount(accountPayload);
      const newAccountId = accountRes.data.id;

      // 3. Create Site (linked to newAccountId)
      const siteUseIds = [];
      const primaryUse = lookups.siteUses?.find(u => u.code === 'PRIMARY');
      const billToUse = lookups.siteUses?.find(u => u.code === 'BILL_TO');
      const shipToUse = lookups.siteUses?.find(u => u.code === 'SHIP_TO');
      if (formData.isPrimary && primaryUse) siteUseIds.push(primaryUse.id);
      if (formData.isBillTo && billToUse) siteUseIds.push(billToUse.id);
      if (formData.isShipTo && shipToUse) siteUseIds.push(shipToUse.id);

      const sitePayload = {
        accountId: newAccountId,
        siteName: formData.siteName,
        addressLine1: formData.addressLine1,
        addressLine2: formData.addressLine2,
        addressLine3: formData.addressLine3,
        city: formData.city,
        state: formData.state,
        postalCode: formData.postalCode,
        country: formData.country,
        siteUseIds
      };

      await entityService.createSite(sitePayload);

      // 4. Contact: If contact first name is provided, update the auto-created placeholder
      if (formData.firstName?.trim()) {
        const contactsRes = await entityService.getContactsByAccount(newAccountId);
        const placeholder = contactsRes.data?.find(c => c.isPlaceholder || c.firstName === 'Missing Information');
        
        const contactPayload = {
          accountId: newAccountId,
          contactTypeId: formData.contactTypeId || null,
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          phone: formData.phone,
          role: formData.role,
          designation: formData.designation
        };

        if (placeholder) {
          await entityService.updateContact(placeholder.id, contactPayload);
        } else {
          await entityService.createContact(contactPayload);
        }
      }

      triggerNotification('success', 'Customer entity hierarchy created successfully.');
      fetchEntities(searchQuery, 0);
      setSelectedEntityId(newEntityId);
      fetchEntityDetails(newEntityId);
    }
  };

  // --- CHILD FORM CRUD HANDLERS ---

  // Accounts CRUD
  const handleAccountSubmit = async (modalData, editId) => {
    if (editId) {
      await entityService.updateAccount(editId, {
        accountName: modalData.accountName,
        natureOfBusiness: modalData.natureOfBusiness,
        creditLimit: modalData.creditLimit ? parseFloat(modalData.creditLimit) : null,
        paymentTermId: modalData.paymentTermId || null,
        billingCycleId: modalData.billingCycleId || null,
        creditClassification: modalData.creditClassification || null,
        creditRisk: modalData.creditRisk || null
      });
      triggerNotification('success', 'Account updated successfully.');
    } else {
      await entityService.createAccount({
        entityId: selectedEntityId,
        accountName: modalData.accountName,
        natureOfBusiness: modalData.natureOfBusiness,
        creditLimit: modalData.creditLimit ? parseFloat(modalData.creditLimit) : null,
        paymentTermId: modalData.paymentTermId || null,
        billingCycleId: modalData.billingCycleId || null,
        creditClassification: modalData.creditClassification || null,
        creditRisk: modalData.creditRisk || null
      });
      triggerNotification('success', 'Account added successfully.');
    }
    fetchEntityDetails(selectedEntityId);
  };

  const handleDeleteAccount = async (acct) => {
    if (selectedEntityDetails.accounts?.length <= 1) {
      alert('Business Rule Violation: Entity must contain at least one Account.');
      return;
    }
    if (window.confirm(`Are you sure you want to delete account "${acct.accountName}"?`)) {
      try {
        await entityService.deleteAccount(acct.id);
        triggerNotification('success', 'Account deleted successfully.');
        fetchEntityDetails(selectedEntityId);
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete account.');
      }
    }
  };

  // Sites CRUD
  const handleSiteSubmit = async (modalData, editId) => {
    if (editId) {
      await entityService.updateSite(editId, {
        siteName: modalData.siteName,
        addressLine1: modalData.addressLine1,
        addressLine2: modalData.addressLine2,
        addressLine3: modalData.addressLine3,
        city: modalData.city,
        state: modalData.state,
        postalCode: modalData.postalCode,
        country: modalData.country,
        siteUseIds: modalData.siteUseIds
      });
      triggerNotification('success', 'Site address updated successfully.');
    } else {
      await entityService.createSite({
        accountId: selectedAccountId,
        siteName: modalData.siteName,
        addressLine1: modalData.addressLine1,
        addressLine2: modalData.addressLine2,
        addressLine3: modalData.addressLine3,
        city: modalData.city,
        state: modalData.state,
        postalCode: modalData.postalCode,
        country: modalData.country,
        siteUseIds: modalData.siteUseIds
      });
      triggerNotification('success', 'Site address added successfully.');
    }
    fetchEntityDetails(selectedEntityId);
  };

  const handleDeleteSite = async (site) => {
    const isPrimary = site.siteUses?.some(u => u.code === 'PRIMARY');
    if (isPrimary && !selectedAccount.sites?.some(s => s.id !== site.id && s.siteUses?.some(u => u.code === 'PRIMARY'))) {
      alert('Business Rule Violation: Cannot delete site because it is the only PRIMARY site for this account.');
      return;
    }
    if (window.confirm(`Are you sure you want to delete site "${site.siteName}"?`)) {
      try {
        await entityService.deleteSite(site.id);
        triggerNotification('success', 'Site address deleted successfully.');
        fetchEntityDetails(selectedEntityId);
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete site.');
      }
    }
  };

  // Contacts CRUD
  const handleContactSubmit = async (modalData, editId) => {
    const payload = {
      accountId: selectedAccountId,
      contactTypeId: modalData.contactTypeId || null,
      firstName: modalData.firstName,
      lastName: modalData.lastName,
      email: modalData.email,
      phone: modalData.phone,
      role: modalData.role,
      designation: modalData.designation
    };

    if (editId) {
      await entityService.updateContact(editId, payload);
      triggerNotification('success', 'Contact details updated successfully.');
    } else {
      await entityService.createContact(payload);
      triggerNotification('success', 'Contact added successfully.');
    }
    fetchEntityDetails(selectedEntityId);
  };

  const handleDeleteContact = async (contact) => {
    if (selectedAccount.contacts?.length <= 1) {
      alert('Business Rule Violation: Every Account must contain at least one Contact.');
      return;
    }
    if (window.confirm(`Are you sure you want to delete contact "${contact.firstName}"?`)) {
      try {
        await entityService.deleteContact(contact.id);
        triggerNotification('success', 'Contact deleted successfully.');
        fetchEntityDetails(selectedEntityId);
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete contact.');
      }
    }
  };

  // Relationships CRUD
  const handleAddRelationship = async (relData) => {
    await entityService.createRelationship(relData);
    triggerNotification('success', 'Entity relationship defined successfully.');
    fetchEntityDetails(selectedEntityId);
  };

  const handleDeleteRelationship = async (relId) => {
    if (window.confirm('Are you sure you want to remove this relationship?')) {
      try {
        await entityService.deleteRelationship(relId);
        triggerNotification('success', 'Relationship removed successfully.');
        fetchEntityDetails(selectedEntityId);
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete relationship.');
      }
    }
  };

  return (
    <MainLayout>
      {/* Toast Notification Banner */}
      {notification && (
        <div className={`notification-banner ${notification.type}`}>
          <Info size={16} />
          <span>{notification.message}</span>
        </div>
      )}

      <div className="entity-management-page">
        {/* Header Title */}
        <div className="page-header-row">
          <div className="page-title-group">
            <h1>Entity Accounts Manager</h1>
            <p>Manage organizations, persons, business accounts, addresses, and contacts.</p>
          </div>
        </div>

        {lookupsError && (
          <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-error)', color: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)' }}>
            <AlertTriangle size={18} />
            <span><strong>Warning:</strong> {lookupsError}. The lookups will fallback to defaults, but save operations might fail. Please ensure the backend server is running.</span>
          </div>
        )}

        {/* Global Search & Action Toolbar */}
        <SearchToolbar
          query={searchQuery}
          onSearchChange={setSearchQuery}
          onAddClick={() => {
            setEditingEntity(null);
            setIsWizardOpen(true);
          }}
        />

        {/* Dynamic Details / Listing Layout */}
        {selectedEntityDetails ? (
          /* DETAILS VIEW (Progressive Disclosure) */
          <div className="details-layout animate-fade-in">
            {/* Back to List row */}
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <button 
                className="btn btn-secondary"
                onClick={() => {
                  setSelectedEntityId(null);
                  setSelectedEntityDetails(null);
                }}
                style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
              >
                <ArrowLeft size={16} />
                <span>Back to Customer Directory</span>
              </button>
            </div>

            {/* Section A: Entity Summary Card */}
            <EntitySummaryCard
              entity={selectedEntityDetails.entitySummary}
              selectedAccount={selectedAccount}
            />

            {/* Main Details Grid */}
            <div className="grid-two-columns">
              {/* Left Column: Accounts & Relationships */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                {/* Section B: Accounts Table */}
                <AccountTable
                  accounts={selectedEntityDetails.accounts}
                  selectedAccountId={selectedAccountId}
                  onAccountSelect={handleAccountSelect}
                  onAddAccountClick={() => {
                    setEditingAccount(null);
                    setIsAccountModalOpen(true);
                  }}
                  onEditAccountClick={(acct) => {
                    setEditingAccount(acct);
                    setIsAccountModalOpen(true);
                  }}
                  onDeleteAccountClick={handleDeleteAccount}
                />

                {/* Section E: Relationships */}
                <RelationshipSection
                  entityId={selectedEntityId}
                  relationships={selectedEntityDetails.relationships}
                  relationshipTypes={lookups.relationshipTypes}
                  allEntities={entities}
                  onAddRelationship={handleAddRelationship}
                  onDeleteRelationship={handleDeleteRelationship}
                />
              </div>

              {/* Right Column: Sites & Contacts (per selected Account) */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                {selectedAccount ? (
                  <>
                    {/* Section C: Sites Table */}
                    <SiteTable
                      sites={selectedAccount.sites || []}
                      onAddSiteClick={() => {
                        setEditingSite(null);
                        setIsSiteModalOpen(true);
                      }}
                      onEditSiteClick={(site) => {
                        setEditingSite(site);
                        setIsSiteModalOpen(true);
                      }}
                      onDeleteSiteClick={handleDeleteSite}
                    />

                    {/* Section D: Contacts Table */}
                    <ContactTable
                      contacts={selectedAccount.contacts || []}
                      onAddContactClick={() => {
                        setEditingContact(null);
                        setIsContactModalOpen(true);
                      }}
                      onEditContactClick={(contact) => {
                        setEditingContact(contact);
                        setIsContactModalOpen(true);
                      }}
                      onDeleteContactClick={handleDeleteContact}
                    />
                  </>
                ) : (
                  <div className="section-card" style={{ padding: 'var(--space-12)', textAlign: 'center', color: 'var(--color-text-secondary)' }}>
                    Please select or add an Account to display Sites and Contacts details.
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          /* PRIMARY DIRECTORY LIST VIEW */
          <EntityTable
            entities={entities}
            loading={listLoading}
            selectedEntityId={selectedEntityId}
            onRowClick={handleEntityRowClick}
            onEditClick={(entity) => {
              setEditingEntity(entity);
              setIsWizardOpen(true);
            }}
            onDeleteClick={handleDeleteEntity}
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={(p) => fetchEntities(searchQuery, p)}
          />
        )}
      </div>

      {/* --- FORM MODALS & DRAWERS --- */}

      {/* 5-Step Customer Wizard Drawer */}
      <MultiStepForm
        isOpen={isWizardOpen}
        onClose={() => setIsWizardOpen(false)}
        onSubmit={handleWizardSubmit}
        editEntity={editingEntity}
        lookups={lookups}
        lookupsLoading={lookupsLoading}
      />

      {/* Account Modal */}
      <AccountFormModal
        isOpen={isAccountModalOpen}
        onClose={() => setIsAccountModalOpen(false)}
        onSubmit={handleAccountSubmit}
        editAccount={editingAccount}
        lookups={lookups}
      />

      {/* Site Address Modal */}
      <SiteFormModal
        isOpen={isSiteModalOpen}
        onClose={() => setIsSiteModalOpen(false)}
        onSubmit={handleSiteSubmit}
        editSite={editingSite}
        lookups={lookups}
      />

      {/* Contact Details Modal */}
      <ContactFormModal
        isOpen={isContactModalOpen}
        onClose={() => setIsContactModalOpen(false)}
        onSubmit={handleContactSubmit}
        editContact={editingContact}
        lookups={lookups}
      />
    </MainLayout>
  );
}
