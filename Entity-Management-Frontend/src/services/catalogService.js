import api from './api';

const catalogService = {
  // --- CATEGORIES ---
  getCategories(page = 0, size = 10, sortBy = 'name', sortDir = 'asc') {
    return api.get(`/api/v1/categories`, {
      params: { page, size, sortBy, sortDir }
    });
  },

  getCategoryTree() {
    return api.get(`/api/v1/categories/tree`);
  },

  getCategoryById(id) {
    return api.get(`/api/v1/categories/${id}`);
  },

  createCategory(data) {
    // data: { code, name, description, parentCategoryId }
    return api.post(`/api/v1/categories`, data);
  },

  updateCategory(id, data) {
    return api.put(`/api/v1/categories/${id}`, data);
  },

  deleteCategory(id) {
    return api.delete(`/api/v1/categories/${id}`);
  },

  // --- CATALOG ITEMS ---
  getItems(page = 0, size = 10, sortBy = 'itemName', sortDir = 'asc') {
    return api.get(`/api/v1/items`, {
      params: { page, size, sortBy, sortDir }
    });
  },

  getItemById(id) {
    return api.get(`/api/v1/items/${id}`);
  },

  createItem(data) {
    // data: { itemNumber, itemName, description, categoryId, primaryUomId, listPrice, isStocked, isInventoryTracked, isService, isSellable, isPurchasable }
    return api.post(`/api/v1/items`, data);
  },

  updateItem(id, data) {
    return api.put(`/api/v1/items/${id}`, data);
  },

  deleteItem(id) {
    return api.delete(`/api/v1/items/${id}`);
  },

  // --- ITEM UOMS (SECONDARY MAPPINGS) ---
  getItemUoms(itemId) {
    return api.get(`/api/v1/items/${itemId}/uoms`);
  },

  addItemUom(itemId, data) {
    // data: { uomId, conversionFactor, isDefault }
    return api.post(`/api/v1/items/${itemId}/uoms`, data);
  },

  updateItemUom(itemId, itemUomId, data) {
    return api.put(`/api/v1/items/${itemId}/uoms/${itemUomId}`, data);
  },

  removeItemUom(itemId, itemUomId) {
    return api.delete(`/api/v1/items/${itemId}/uoms/${itemUomId}`);
  },

  // --- UOM MASTER DATA ---
  getUoms(page = 0, size = 1000, sortBy = 'code', sortDir = 'asc') {
    // Large page size defaults for lookup listings
    return api.get(`/api/v1/uoms`, {
      params: { page, size, sortBy, sortDir }
    });
  },

  getUomById(id) {
    return api.get(`/api/v1/uoms/${id}`);
  },

  createUom(data) {
    // data: { code, name }
    return api.post(`/api/v1/uoms`, data);
  },

  updateUom(id, data) {
    return api.put(`/api/v1/uoms/${id}`, data);
  },

  deleteUom(id) {
    return api.delete(`/api/v1/uoms/${id}`);
  },

  // --- WAREHOUSES ---
  getWarehouses(page = 0, size = 1000, sortBy = 'name', sortDir = 'asc') {
    return api.get(`/api/v1/warehouses`, {
      params: { page, size, sortBy, sortDir }
    });
  },

  getWarehouseById(id) {
    return api.get(`/api/v1/warehouses/${id}`);
  },

  createWarehouse(data) {
    // data: { code, name, address }
    return api.post(`/api/v1/warehouses`, data);
  },

  updateWarehouse(id, data) {
    return api.put(`/api/v1/warehouses/${id}`, data);
  },

  deleteWarehouse(id) {
    return api.delete(`/api/v1/warehouses/${id}`);
  },

  // --- INVENTORY BALANCES ---
  getBalances(itemId = null, warehouseId = null) {
    const params = {};
    if (itemId) params.itemId = itemId;
    if (warehouseId) params.warehouseId = warehouseId;
    return api.get(`/api/v1/inventory/balances`, { params });
  },

  // --- INVENTORY TRANSACTIONS ---
  getTransactions(itemId = null, warehouseId = null, page = 0, size = 10, sortBy = 'transactionDate', sortDir = 'desc') {
    const params = { page, size, sortBy, sortDir };
    if (itemId) params.itemId = itemId;
    if (warehouseId) params.warehouseId = warehouseId;
    return api.get(`/api/v1/inventory/transactions`, { params });
  },

  createTransaction(data) {
    // data: { itemId, warehouseId, transactionType, referenceType, referenceId, quantity, uomId, remarks }
    return api.post(`/api/v1/inventory/transactions`, data);
  },

  transferStock(data) {
    // data: { itemId, sourceWarehouseId, destinationWarehouseId, quantity, uomId, remarks }
    return api.post(`/api/v1/inventory/transactions/transfer`, data);
  }
};

export default catalogService;
