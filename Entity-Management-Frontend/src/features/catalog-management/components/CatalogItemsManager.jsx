import React, { useState, useEffect, useCallback } from 'react';
import { Search, Plus, ArrowLeft, Trash2, Edit2, Scale, BarChart2, ShieldAlert, Package, Check, X } from 'lucide-react';
import catalogService from '../../../services/catalogService';
import CatalogItemFormModal from './CatalogItemFormModal';
import ItemUomFormModal from './ItemUomFormModal';
import '../CatalogManagement.css';

export default function CatalogItemsManager({ triggerNotification }) {
  // Master lists for lookups
  const [categories, setCategories] = useState([]);
  const [uoms, setUoms] = useState([]);

  // Items search & paginate list state
  const [items, setItems] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('');
  const [selectedTypeFilter, setSelectedTypeFilter] = useState(''); // stocked, service, inventoryTracked
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Selection & Details hierarchy state
  const [selectedItemId, setSelectedItemId] = useState(null);
  const [selectedItemDetails, setSelectedItemDetails] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [itemUoms, setItemUoms] = useState([]);
  const [inventoryBalances, setInventoryBalances] = useState([]);

  // Modals state
  const [isItemModalOpen, setIsItemModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [isUomModalOpen, setIsUomModalOpen] = useState(false);
  const [editingItemUom, setEditingItemUom] = useState(null);

  // Fetch standard lookups
  const fetchLookups = useCallback(async () => {
    try {
      const catRes = await catalogService.getCategories(0, 1000, 'name', 'asc');
      setCategories(catRes.data?.content || []);
      const uomRes = await catalogService.getUoms(0, 1000, 'code', 'asc');
      setUoms(uomRes.data?.content || []);
    } catch (err) {
      triggerNotification('error', 'Failed to retrieve master list categories or UOMs.');
    }
  }, [triggerNotification]);

  // Fetch Items list
  const fetchItemsList = useCallback(async (queryVal = '', categoryIdVal = '', typeVal = '', pageNum = 0) => {
    setLoading(true);
    try {
      const res = await catalogService.getItems(pageNum, 10, 'itemName', 'asc');
      const apiData = res.data || {};
      let filteredContent = apiData.content || [];

      // Backend returns standard paginated list. Let's apply filters on the client if query active
      if (queryVal.trim()) {
        const queryLower = queryVal.toLowerCase();
        filteredContent = filteredContent.filter(item =>
          item.itemName.toLowerCase().includes(queryLower) ||
          item.itemNumber.toLowerCase().includes(queryLower) ||
          (item.description && item.description.toLowerCase().includes(queryLower))
        );
      }

      if (categoryIdVal) {
        filteredContent = filteredContent.filter(item => item.categoryId === categoryIdVal);
      }

      if (typeVal) {
        if (typeVal === 'stocked') filteredContent = filteredContent.filter(item => item.isStocked);
        else if (typeVal === 'service') filteredContent = filteredContent.filter(item => item.isService);
        else if (typeVal === 'inventoryTracked') filteredContent = filteredContent.filter(item => item.isInventoryTracked);
      }

      setItems(filteredContent);
      setTotalPages(apiData.totalPages || 0);
      setTotalElements(filteredContent.length);
      setPage(pageNum);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to fetch catalog items.');
    } finally {
      setLoading(false);
    }
  }, [triggerNotification]);

  // Load detailed items hierarchy
  const fetchCompleteItemDetails = async (itemId) => {
    setDetailsLoading(true);
    try {
      const res = await catalogService.getItemById(itemId);
      setSelectedItemDetails(res.data || null);

      // Fetch secondary conversions
      const uomRes = await catalogService.getItemUoms(itemId);
      setItemUoms(uomRes.data || []);

      // Fetch read-only inventory balances
      const balRes = await catalogService.getBalances(itemId, null);
      setInventoryBalances(balRes.data || []);
    } catch (err) {
      triggerNotification('error', 'Failed to retrieve detailed item specifications.');
    } finally {
      setDetailsLoading(false);
    }
  };

  useEffect(() => {
    fetchLookups();
  }, [fetchLookups]);

  // Debounced search trigger
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchItemsList(searchQuery, selectedCategoryId, selectedTypeFilter, 0);
    }, 400);
    return () => clearTimeout(timer);
  }, [searchQuery, selectedCategoryId, selectedTypeFilter, fetchItemsList]);

  // Item form submissions
  const handleItemSubmit = async (payload, editId) => {
    try {
      if (editId) {
        await catalogService.updateItem(editId, payload);
        triggerNotification('success', 'Catalog item updated successfully.');
        if (selectedItemId === editId) {
          fetchCompleteItemDetails(editId);
        }
      } else {
        await catalogService.createItem(payload);
        triggerNotification('success', 'Catalog item created successfully.');
      }
      fetchItemsList(searchQuery, selectedCategoryId, selectedTypeFilter, page);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to save item.');
      throw err;
    }
  };

  const handleDeleteItem = async (item) => {
    if (window.confirm(`Are you sure you want to delete item "${item.itemName}"?`)) {
      try {
        await catalogService.deleteItem(item.id);
        triggerNotification('success', 'Catalog item deleted successfully.');
        fetchItemsList(searchQuery, selectedCategoryId, selectedTypeFilter, page);
        if (selectedItemId === item.id) {
          setSelectedItemId(null);
          setSelectedItemDetails(null);
        }
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete item.');
      }
    }
  };

  // UOM mappings submissions
  const handleUomSubmit = async (payload, editId) => {
    try {
      if (editId) {
        await catalogService.updateItemUom(selectedItemId, editId, payload);
        triggerNotification('success', 'UOM conversion factor updated.');
      } else {
        await catalogService.addItemUom(selectedItemId, payload);
        triggerNotification('success', 'Secondary UOM conversion mapped successfully.');
      }
      fetchCompleteItemDetails(selectedItemId);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to save UOM conversion mapping.');
      throw err;
    }
  };

  const handleDeleteUom = async (mappingId) => {
    if (window.confirm('Are you sure you want to remove this secondary conversion factor?')) {
      try {
        await catalogService.removeItemUom(selectedItemId, mappingId);
        triggerNotification('success', 'Secondary conversion factor deleted.');
        fetchCompleteItemDetails(selectedItemId);
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to remove UOM conversion.');
      }
    }
  };

  // Header Title component
  const formatPrice = (price) => {
    if (price === undefined || price === null) return '$0.00';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(price);
  };

  return (
    <div className="entity-management-page animate-fade-in">
      {selectedItemDetails ? (
        /* ==================== Progressive Disclosure: Detailed Dashboard ==================== */
        <div className="details-layout animate-fade-in">
          <div>
            <button
              className="btn btn-secondary"
              onClick={() => {
                setSelectedItemId(null);
                setSelectedItemDetails(null);
              }}
              style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <ArrowLeft size={16} />
              <span>Back to Item Catalog Directory</span>
            </button>
          </div>

          {/* Item summary card */}
          <div className="summary-panel">
            <div className="summary-left">
              <div className="summary-avatar" style={{ backgroundColor: 'var(--color-primary-light)' }}>
                <Package size={24} style={{ color: 'var(--color-primary)' }} />
              </div>
              <div className="summary-title">
                <h2>{selectedItemDetails.itemName}</h2>
                <div style={{ display: 'flex', gap: '12px', fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginTop: '4px' }}>
                  <span><strong>SKU:</strong> {selectedItemDetails.itemNumber}</span>
                  <span>•</span>
                  <span><strong>Category:</strong> {selectedItemDetails.categoryName}</span>
                  <span>•</span>
                  <span><strong>Primary UOM:</strong> {selectedItemDetails.primaryUomCode}</span>
                </div>
              </div>
            </div>

            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.75rem', textTransform: 'uppercase', color: 'var(--color-text-muted)', fontWeight: 600 }}>List Price</div>
              <div style={{ fontSize: '1.75rem', fontWeight: 700, color: 'var(--color-primary)' }}>
                {formatPrice(selectedItemDetails.listPrice)}
              </div>
            </div>
          </div>

          {detailsLoading ? (
            <div className="table-container" style={{ padding: 'var(--space-8)', textAlign: 'center' }}>
              <div className="spinner spinner-lg" style={{ margin: '0 auto' }} />
              <p style={{ marginTop: 'var(--space-4)' }}>Loading item details...</p>
            </div>
          ) : (
            <div className="grid-two-columns">
              {/* Left Column: Properties & Secondary UOM Convs */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                {/* Section B1: Item Capabilities */}
                <div className="section-card" style={{ backgroundColor: 'var(--color-bg-surface)', border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)', padding: 'var(--space-4)' }}>
                  <h3 style={{ borderBottom: '1px solid var(--color-border)', paddingBottom: 'var(--space-2)', marginBottom: 'var(--space-3)' }}>
                    Item Properties
                  </h3>
                  
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-3)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {selectedItemDetails.isService ? <Check size={16} style={{ color: 'var(--color-success)' }} /> : <X size={16} style={{ color: 'var(--color-text-muted)' }} />}
                      <span style={{ fontSize: '0.9rem' }}>Service Item</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {selectedItemDetails.isStocked ? <Check size={16} style={{ color: 'var(--color-success)' }} /> : <X size={16} style={{ color: 'var(--color-text-muted)' }} />}
                      <span style={{ fontSize: '0.9rem' }}>Stocked Product</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {selectedItemDetails.isInventoryTracked ? <Check size={16} style={{ color: 'var(--color-success)' }} /> : <X size={16} style={{ color: 'var(--color-text-muted)' }} />}
                      <span style={{ fontSize: '0.9rem' }}>Inventory Tracked</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {selectedItemDetails.isSellable ? <Check size={16} style={{ color: 'var(--color-success)' }} /> : <X size={16} style={{ color: 'var(--color-text-muted)' }} />}
                      <span style={{ fontSize: '0.9rem' }}>Sellable (Customers)</span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {selectedItemDetails.isPurchasable ? <Check size={16} style={{ color: 'var(--color-success)' }} /> : <X size={16} style={{ color: 'var(--color-text-muted)' }} />}
                      <span style={{ fontSize: '0.9rem' }}>Purchasable (Vendors)</span>
                    </div>
                  </div>
                </div>

                {/* Section B2: UOM Management conversions */}
                <div className="table-container">
                  <div className="modal-header" style={{ borderBottom: '1px solid var(--color-border)', padding: 'var(--space-4)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3 style={{ fontSize: '1.05rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <Scale size={18} style={{ color: 'var(--color-primary)' }} />
                      <span>Unit of Measure Conversions</span>
                    </h3>
                    <button
                      className="btn btn-secondary"
                      onClick={() => {
                        setEditingItemUom(null);
                        setIsUomModalOpen(true);
                      }}
                      style={{ padding: '4px 10px', fontSize: '0.8rem' }}
                    >
                      <Plus size={14} />
                      <span>Add Conversion</span>
                    </button>
                  </div>
                  <div style={{ padding: 'var(--space-4)' }}>
                    {itemUoms.length === 0 ? (
                      <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem', textAlign: 'center', padding: 'var(--space-3)' }}>
                        No secondary conversions mapped. Currently transactable in <strong>{selectedItemDetails.primaryUomCode}</strong> only.
                      </p>
                    ) : (
                      <div className="uom-conversions-list">
                        {itemUoms.map((mapping) => (
                          <div key={mapping.id} className="uom-conversion-row">
                            <div>
                              <strong>1 {mapping.uomCode}</strong> = {mapping.conversionFactor} {selectedItemDetails.primaryUomCode}
                              {mapping.isDefault && (
                                <span className="badge badge-success" style={{ marginLeft: 'var(--space-2)', fontSize: '0.65rem' }}>
                                  Default Secondary
                                </span>
                              )}
                            </div>
                            <div className="action-cell">
                              <button
                                className="row-action-btn"
                                onClick={() => {
                                  setEditingItemUom(mapping);
                                  setIsUomModalOpen(true);
                                }}
                                title="Edit Conversion Factor"
                              >
                                <Edit2 size={13} />
                              </button>
                              <button
                                className="row-action-btn delete-btn"
                                onClick={() => handleDeleteUom(mapping.id)}
                                title="Delete Mapping"
                              >
                                <Trash2 size={13} />
                              </button>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* Right Column: Inventory Balances (Read Only) */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
                <div className="table-container">
                  <div className="modal-header" style={{ borderBottom: '1px solid var(--color-border)', padding: 'var(--space-4)' }}>
                    <h3 style={{ fontSize: '1.05rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <BarChart2 size={18} style={{ color: 'var(--color-success)' }} />
                      <span>Depot Inventory Balances</span>
                    </h3>
                  </div>
                  
                  {!selectedItemDetails.isInventoryTracked ? (
                    <div style={{ padding: 'var(--space-6)', textAlign: 'center', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                      <ShieldAlert size={36} style={{ color: 'var(--color-warning)', marginBottom: 'var(--space-2)' }} />
                      <p>Inventory balance tracking is not enabled for this item type (Service or Stock-Untracked).</p>
                    </div>
                  ) : (
                    <table className="enterprise-table">
                      <thead>
                        <tr>
                          <th>Warehouse</th>
                          <th style={{ textAlign: 'right' }}>On Hand</th>
                          <th style={{ textAlign: 'right' }}>Reserved</th>
                          <th style={{ textAlign: 'right' }}>Available</th>
                        </tr>
                      </thead>
                      <tbody>
                        {inventoryBalances.length === 0 ? (
                          <tr>
                            <td colSpan="4" style={{ textAlign: 'center', color: 'var(--color-text-muted)', padding: 'var(--space-4)' }}>
                              No stock recorded in any warehouse.
                            </td>
                          </tr>
                        ) : (
                          inventoryBalances.map((bal) => (
                            <tr key={bal.id}>
                              <td style={{ fontWeight: 600 }}>
                                {bal.warehouseName} <span style={{ color: 'var(--color-text-muted)', fontFamily: 'monospace', fontSize: '0.8rem' }}>({bal.warehouseCode})</span>
                              </td>
                              <td style={{ textAlign: 'right', fontWeight: 600 }}>{bal.quantityOnHand}</td>
                              <td style={{ textAlign: 'right', color: 'var(--color-warning)' }}>{bal.reservedQty}</td>
                              <td style={{ textAlign: 'right', color: 'var(--color-success)', fontWeight: 600 }}>{bal.availableQty}</td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      ) : (
        /* ==================== Catalog Directory Table List View ==================== */
        <>
          <div className="page-header-row">
            <div className="page-title-group">
              <h1>Product & Service Catalog</h1>
              <p>Maintain items, price lists, Units of Measure conversion matrixes, and capabilities settings.</p>
            </div>
          </div>

          {/* Search bar & Filters */}
          <div className="toolbar-card">
            <div className="search-input-wrapper">
              <Search className="search-icon-inside" size={18} />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search items by name, number, or description..."
                aria-label="Search catalog items"
              />
            </div>

            <div className="filters-row">
              <select
                value={selectedCategoryId}
                onChange={(e) => setSelectedCategoryId(e.target.value)}
                className="filter-select"
              >
                <option value="">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>

              <select
                value={selectedTypeFilter}
                onChange={(e) => setSelectedTypeFilter(e.target.value)}
                className="filter-select"
              >
                <option value="">All Capabilities</option>
                <option value="stocked">Stocked Only</option>
                <option value="service">Service Only</option>
                <option value="inventoryTracked">Inventory Tracked</option>
              </select>
            </div>

            <button
              className="btn btn-primary"
              onClick={() => {
                setEditingItem(null);
                setIsItemModalOpen(true);
              }}
              style={{ alignSelf: 'flex-start' }}
            >
              <Plus size={18} />
              <span>Add Item</span>
            </button>
          </div>

          {/* Catalog items table */}
          <div className="table-container animate-fade-in">
            <table className="enterprise-table">
              <thead>
                <tr>
                  <th>SKU / Number</th>
                  <th>Item Name</th>
                  <th>Category</th>
                  <th>Primary UOM</th>
                  <th style={{ textAlign: 'right' }}>List Price</th>
                  <th>Capabilities</th>
                  <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  Array.from({ length: 5 }).map((_, idx) => (
                    <tr key={idx}>
                      <td><div className="skeleton" style={{ width: '80px', height: '20px' }} /></td>
                      <td><div className="skeleton" style={{ width: '180px', height: '20px' }} /></td>
                      <td><div className="skeleton" style={{ width: '100px', height: '20px' }} /></td>
                      <td><div className="skeleton" style={{ width: '60px', height: '20px' }} /></td>
                      <td><div className="skeleton" style={{ width: '80px', height: '20px', marginLeft: 'auto' }} /></td>
                      <td><div className="skeleton" style={{ width: '150px', height: '20px' }} /></td>
                      <td><div className="skeleton" style={{ width: '60px', height: '20px', marginLeft: 'auto' }} /></td>
                    </tr>
                  ))
                ) : items.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                      No items found matching the selected filters.
                    </td>
                  </tr>
                ) : (
                  items.map((item) => (
                    <tr
                      key={item.id}
                      className="clickable-row"
                      onClick={() => {
                        setSelectedItemId(item.id);
                        fetchCompleteItemDetails(item.id);
                      }}
                    >
                      <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{item.itemNumber}</td>
                      <td style={{ fontWeight: 600 }}>{item.itemName}</td>
                      <td>{item.categoryName}</td>
                      <td>{item.primaryUomCode}</td>
                      <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatPrice(item.listPrice)}</td>
                      <td>
                        <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                          {item.isService && <span className="badge badge-info" style={{ fontSize: '0.65rem' }}>Service</span>}
                          {item.isStocked && <span className="badge badge-primary" style={{ fontSize: '0.65rem' }}>Stocked</span>}
                          {item.isInventoryTracked && <span className="badge badge-success" style={{ fontSize: '0.65rem' }}>Tracked</span>}
                          {item.isSellable && <span className="badge badge-primary" style={{ fontSize: '0.65rem', backgroundColor: 'rgba(99,102,241,0.1)' }}>Sellable</span>}
                        </div>
                      </td>
                      <td className="action-cell" onClick={(e) => e.stopPropagation()}>
                        <button
                          className="row-action-btn"
                          onClick={() => {
                            setEditingItem(item);
                            setIsItemModalOpen(true);
                          }}
                          title="Edit Item Specs"
                        >
                          <Edit2 size={15} />
                        </button>
                        <button
                          className="row-action-btn delete-btn"
                          onClick={() => handleDeleteItem(item)}
                          title="Soft Delete Item"
                        >
                          <Trash2 size={15} />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* Item specs form modal */}
      <CatalogItemFormModal
        isOpen={isItemModalOpen}
        onClose={() => setIsItemModalOpen(false)}
        onSubmit={handleItemSubmit}
        editItem={editingItem}
        categories={categories}
        uoms={uoms}
      />

      {/* Item UOM mappings conversions form modal */}
      <ItemUomFormModal
        isOpen={isUomModalOpen}
        onClose={() => setIsUomModalOpen(false)}
        onSubmit={handleUomSubmit}
        editItemUom={editingItemUom}
        itemPrimaryUomId={selectedItemDetails?.primaryUomId}
        itemPrimaryUomCode={selectedItemDetails?.primaryUomCode}
        uoms={uoms}
        existingItemUoms={itemUoms}
      />
    </div>
  );
}
