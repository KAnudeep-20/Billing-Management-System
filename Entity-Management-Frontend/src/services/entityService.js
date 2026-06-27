import api from './api';

const entityService = {
  // --- ENTITY ENDPOINTS ---
  getEntities(page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc') {
    return api.get(`/api/v1/entities`, {
      params: { page, size, sortBy, sortDir }
    });
  },

  getEntityById(id) {
    return api.get(`/api/v1/entities/${id}`);
  },

  getCompleteEntityDetails(id) {
    return api.get(`/api/v1/entities/${id}/complete`);
  },

  createEntity(data) {
    // data: { entityCategory, entityTypeCodes, organizationDetails, personDetails }
    return api.post(`/api/v1/entities`, data);
  },

  updateEntity(id, data) {
    return api.put(`/api/v1/entities/${id}`, data);
  },

  deleteEntity(id) {
    return api.delete(`/api/v1/entities/${id}`);
  },

  // --- SEARCH ENDPOINTS ---
  searchEntities(query = '', page = 0, size = 20) {
    return api.get(`/api/v1/search/entities`, {
      params: { query, page, size }
    });
  },

  // --- ACCOUNT ENDPOINTS ---
  createAccount(data) {
    // data: { entityId, accountName, natureOfBusiness, creditLimit, paymentTermId, billingCycleId, creditClassification, creditRisk }
    return api.post(`/api/v1/accounts`, data);
  },

  updateAccount(id, data) {
    return api.put(`/api/v1/accounts/${id}`, data);
  },

  getAccountsByEntity(entityId) {
    return api.get(`/api/v1/accounts/entity/${entityId}`);
  },

  deleteAccount(id) {
    return api.delete(`/api/v1/accounts/${id}`);
  },

  // --- SITE ENDPOINTS ---
  createSite(data) {
    // data: { accountId, siteName, addressLine1, addressLine2, addressLine3, city, state, postalCode, country, siteUseIds }
    return api.post(`/api/v1/sites`, data);
  },

  updateSite(id, data) {
    return api.put(`/api/v1/sites/${id}`, data);
  },

  getSitesByAccount(accountId) {
    return api.get(`/api/v1/sites/account/${accountId}`);
  },

  deleteSite(id) {
    return api.delete(`/api/v1/sites/${id}`);
  },

  // --- CONTACT ENDPOINTS ---
  createContact(data) {
    // data: { accountId, contactTypeId, firstName, lastName, email, phone, role, designation }
    return api.post(`/api/v1/contacts`, data);
  },

  updateContact(id, data) {
    return api.put(`/api/v1/contacts/${id}`, data);
  },

  getContactsByAccount(accountId) {
    return api.get(`/api/v1/contacts/account/${accountId}`);
  },

  deleteContact(id) {
    return api.delete(`/api/v1/contacts/${id}`);
  },

  // --- RELATIONSHIP ENDPOINTS ---
  createRelationship(data) {
    // data: { subjectEntityId, relationshipTypeId, objectEntityId }
    return api.post(`/api/v1/entity-relationships`, data);
  },

  deleteRelationship(id) {
    return api.delete(`/api/v1/entity-relationships/${id}`);
  },

  getRelationships(subjectId, objectId, entityId) {
    const params = {};
    if (subjectId) params.subjectId = subjectId;
    if (objectId) params.objectId = objectId;
    if (entityId) params.entityId = entityId;
    return api.get(`/api/v1/entity-relationships`, { params });
  },

  // --- LOOKUP ENDPOINTS ---
  getEntityTypeLookup() {
    return api.get(`/api/v1/entity-types/lookup`);
  },

  getRelationshipTypeLookup() {
    return api.get(`/api/v1/relationship-types/lookup`);
  },

  getSiteUseLookup() {
    return api.get(`/api/v1/site-uses/lookup`);
  },

  getPaymentTermLookup() {
    return api.get(`/api/v1/payment-terms/lookup`);
  },

  getContactTypeLookup() {
    return api.get(`/api/v1/contact-types/lookup`);
  },

  getBillingCycleLookup() {
    return api.get(`/api/v1/billing-cycles/lookup`);
  }
};

export default entityService;
