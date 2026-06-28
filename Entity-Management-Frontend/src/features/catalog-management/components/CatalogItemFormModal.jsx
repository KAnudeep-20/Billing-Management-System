import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export default function CatalogItemFormModal({
  isOpen,
  onClose,
  onSubmit,
  editItem,
  categories = [],
  uoms = []
}) {
  const [formData, setFormData] = useState({
    itemNumber: '',
    itemName: '',
    description: '',
    categoryId: '',
    primaryUomId: '',
    listPrice: '',
    isStocked: false,
    isInventoryTracked: false,
    isService: false,
    isSellable: true,
    isPurchasable: true
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (editItem) {
      setFormData({
        itemNumber: editItem.itemNumber || '',
        itemName: editItem.itemName || '',
        description: editItem.description || '',
        categoryId: editItem.categoryId || '',
        primaryUomId: editItem.primaryUomId || '',
        listPrice: editItem.listPrice ? editItem.listPrice.toString() : '',
        isStocked: editItem.isStocked || false,
        isInventoryTracked: editItem.isInventoryTracked || false,
        isService: editItem.isService || false,
        isSellable: editItem.isSellable !== undefined ? editItem.isSellable : true,
        isPurchasable: editItem.isPurchasable !== undefined ? editItem.isPurchasable : true
      });
    } else {
      setFormData({
        itemNumber: '',
        itemName: '',
        description: '',
        categoryId: '',
        primaryUomId: '',
        listPrice: '',
        isStocked: false,
        isInventoryTracked: false,
        isService: false,
        isSellable: true,
        isPurchasable: true
      });
    }
    setErrors({});
  }, [editItem, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const newVal = type === 'checkbox' ? checked : value;

    setFormData((prev) => {
      let updated = { ...prev, [name]: newVal };

      // Apply business rules dynamically to the form state to guide user input:
      if (name === 'isInventoryTracked' && checked) {
        // Rule: An inventory-tracked item must also be stocked.
        updated.isStocked = true;
        updated.isService = false; // Stocked cannot be service
      }
      
      if (name === 'isStocked' && checked) {
        // Rule: A stocked item cannot be a service item.
        updated.isService = false;
      }
      
      if (name === 'isStocked' && !checked) {
        // If unstocking, it can't be inventory tracked
        updated.isInventoryTracked = false;
      }

      if (name === 'isService' && checked) {
        // Rule: A service item cannot be stocked or inventory-tracked.
        updated.isStocked = false;
        updated.isInventoryTracked = false;
      }

      return updated;
    });

    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.itemNumber.trim()) newErrors.itemNumber = 'Item number is required.';
    else if (formData.itemNumber.length > 50) newErrors.itemNumber = 'Item number must not exceed 50 characters.';

    if (!formData.itemName.trim()) newErrors.itemName = 'Item name is required.';
    else if (formData.itemName.length > 100) newErrors.itemName = 'Item name must not exceed 100 characters.';

    if (formData.description && formData.description.length > 255) {
      newErrors.description = 'Description must not exceed 255 characters.';
    }

    if (!formData.categoryId) newErrors.categoryId = 'Category is required.';
    if (!formData.primaryUomId) newErrors.primaryUomId = 'Primary Unit of Measure is required.';

    if (!formData.listPrice || isNaN(formData.listPrice) || parseFloat(formData.listPrice) < 0) {
      newErrors.listPrice = 'List price must be zero or a positive number.';
    }

    // Business rule checks
    if (formData.isInventoryTracked && !formData.isStocked) {
      newErrors.isInventoryTracked = 'An inventory-tracked item must also be stocked.';
    }
    if (formData.isStocked && formData.isService) {
      newErrors.isService = 'A stocked item cannot be a service item.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      const payload = {
        itemNumber: formData.itemNumber.trim().toUpperCase(),
        itemName: formData.itemName.trim(),
        description: formData.description.trim() || null,
        categoryId: formData.categoryId,
        primaryUomId: formData.primaryUomId,
        listPrice: parseFloat(formData.listPrice),
        isStocked: formData.isStocked,
        isInventoryTracked: formData.isInventoryTracked,
        isService: formData.isService,
        isSellable: formData.isSellable,
        isPurchasable: formData.isPurchasable
      };
      await onSubmit(payload, editItem?.id);
      onClose();
    } catch (err) {
      console.error(err);
      setErrors({ apiError: err.message || 'Operation failed. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  // Business Rule: Items can only be assigned to leaf categories (categories with no subcategories)
  const leafCategories = categories.filter((cat) => !cat.hasSubCategories);

  return createPortal(
    <div className="catalog-drawer-overlay" onClick={onClose}>
      <div className="catalog-drawer-container" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-header">
          <h2>{editItem ? 'Edit Catalog Item' : 'Create Catalog Item'}</h2>
          <button className="drawer-close-btn" onClick={onClose} aria-label="Close drawer">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden' }}>
          <div className="drawer-body">
            {errors.apiError && (
              <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-error)', color: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)', marginBottom: 'var(--space-4)', padding: 'var(--space-2)' }}>
                <span>{errors.apiError}</span>
              </div>
            )}

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="itemNumber" className="required">Item Number / SKU</label>
                <input
                  type="text"
                  id="itemNumber"
                  name="itemNumber"
                  value={formData.itemNumber}
                  onChange={handleChange}
                  placeholder="e.g. IT-1004"
                  disabled={loading}
                  className={`form-control ${errors.itemNumber ? 'error' : ''}`}
                />
                {errors.itemNumber && <span className="error-text">{errors.itemNumber}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="itemName" className="required">Item Name</label>
                <input
                  type="text"
                  id="itemName"
                  name="itemName"
                  value={formData.itemName}
                  onChange={handleChange}
                  placeholder="e.g. Laser Printer Pro"
                  disabled={loading}
                  className={`form-control ${errors.itemName ? 'error' : ''}`}
                />
                {errors.itemName && <span className="error-text">{errors.itemName}</span>}
              </div>
            </div>

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="categoryId" className="required">Category (Leaf Only)</label>
                <select
                  id="categoryId"
                  name="categoryId"
                  value={formData.categoryId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.categoryId ? 'error' : ''}`}
                >
                  <option value="">-- Select Category --</option>
                  {leafCategories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name} ({cat.code})
                    </option>
                  ))}
                </select>
                {errors.categoryId && <span className="error-text">{errors.categoryId}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="primaryUomId" className="required">Primary Unit of Measure</label>
                <select
                  id="primaryUomId"
                  name="primaryUomId"
                  value={formData.primaryUomId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.primaryUomId ? 'error' : ''}`}
                >
                  <option value="">-- Select UOM --</option>
                  {uoms.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.name} ({u.code})
                    </option>
                  ))}
                </select>
                {errors.primaryUomId && <span className="error-text">{errors.primaryUomId}</span>}
              </div>
            </div>

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="listPrice" className="required">List Price ($)</label>
                <input
                  type="number"
                  step="0.01"
                  id="listPrice"
                  name="listPrice"
                  value={formData.listPrice}
                  onChange={handleChange}
                  placeholder="0.00"
                  disabled={loading}
                  className={`form-control ${errors.listPrice ? 'error' : ''}`}
                />
                {errors.listPrice && <span className="error-text">{errors.listPrice}</span>}
              </div>

              {/* Spacer */}
              <div />
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Provide a detailed description of the catalog item..."
                rows={2}
                disabled={loading}
                className={`form-control ${errors.description ? 'error' : ''}`}
                style={{ resize: 'vertical' }}
              />
              {errors.description && <span className="error-text">{errors.description}</span>}
            </div>

            {/* Behavior and Lifecycle Flags */}
            <div className="form-section" style={{ borderTop: '1px solid var(--color-border)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-4)' }}>
              <h3 style={{ fontSize: '0.95rem', marginBottom: 'var(--space-3)' }}>Item Behavior & Capabilities</h3>
              
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 'var(--space-3)' }}>
                <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem' }}>
                  <input
                    type="checkbox"
                    name="isService"
                    checked={formData.isService}
                    onChange={handleChange}
                    disabled={loading || formData.isStocked}
                  />
                  <span>Service / Intangible Item</span>
                </label>

                <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem' }}>
                  <input
                    type="checkbox"
                    name="isStocked"
                    checked={formData.isStocked}
                    onChange={handleChange}
                    disabled={loading || formData.isService || formData.isInventoryTracked}
                  />
                  <span>Stocked Product (Physical)</span>
                </label>

                <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem' }}>
                  <input
                    type="checkbox"
                    name="isInventoryTracked"
                    checked={formData.isInventoryTracked}
                    onChange={handleChange}
                    disabled={loading || formData.isService}
                  />
                  <span>Inventory Tracked (Ledger)</span>
                </label>

                <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem' }}>
                  <input
                    type="checkbox"
                    name="isSellable"
                    checked={formData.isSellable}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  <span>Can be Sold (Customer Facing)</span>
                </label>

                <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem' }}>
                  <input
                    type="checkbox"
                    name="isPurchasable"
                    checked={formData.isPurchasable}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  <span>Can be Purchased (Vendor Facing)</span>
                </label>
              </div>
            </div>
          </div>

          <div className="drawer-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? <div className="spinner" /> : editItem ? 'Save Item' : 'Create Item'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
