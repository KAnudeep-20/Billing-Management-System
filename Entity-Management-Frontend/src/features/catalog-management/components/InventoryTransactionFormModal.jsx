import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export default function InventoryTransactionFormModal({
  isOpen,
  onClose,
  onSubmit,
  items = [],
  warehouses = [],
  uoms = []
}) {
  const [formData, setFormData] = useState({
    itemId: '',
    warehouseId: '',
    transactionType: '',
    referenceType: '',
    referenceId: '',
    quantity: '',
    uomId: '',
    remarks: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setFormData({
      itemId: '',
      warehouseId: '',
      transactionType: '',
      referenceType: '',
      referenceId: '',
      quantity: '',
      uomId: '',
      remarks: ''
    });
    setErrors({});
  }, [isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const updated = { ...prev, [name]: value };

      // Auto-set the UOM select value to the item's primary UOM when item is chosen, as a helper
      if (name === 'itemId' && value) {
        const itemObj = items.find((i) => i.id === value);
        if (itemObj) {
          updated.uomId = itemObj.primaryUomId || '';
        }
      }

      return updated;
    });

    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.itemId) newErrors.itemId = 'Product item is required.';
    if (!formData.warehouseId) newErrors.warehouseId = 'Warehouse depot is required.';
    if (!formData.transactionType) newErrors.transactionType = 'Transaction ledger type is required.';

    if (!formData.quantity || isNaN(formData.quantity) || parseFloat(formData.quantity) <= 0.0001) {
      newErrors.quantity = 'Quantity must be greater than zero.';
    }

    if (!formData.uomId) newErrors.uomId = 'Unit of Measure is required.';

    if (formData.referenceId) {
      // Validate referenceId is a valid UUID
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
      if (!uuidRegex.test(formData.referenceId.trim())) {
        newErrors.referenceId = 'Reference ID must be a valid UUID format (or left blank).';
      }
    }

    if (formData.remarks && formData.remarks.length > 255) {
      newErrors.remarks = 'Remarks must not exceed 255 characters.';
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
        itemId: formData.itemId,
        warehouseId: formData.warehouseId,
        transactionType: formData.transactionType,
        referenceType: formData.referenceType.trim() || null,
        referenceId: formData.referenceId.trim() || null,
        quantity: parseFloat(formData.quantity),
        uomId: formData.uomId,
        remarks: formData.remarks.trim() || null
      };
      await onSubmit(payload);
      onClose();
    } catch (err) {
      console.error(err);
      setErrors({ apiError: err.message || 'Operation failed. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  // Exclude transfer out and in because those should be performed using StockTransferFormModal atomically
  const transactionTypes = [
    { code: 'PURCHASE_RECEIPT', label: 'Purchase Receipt (+ On Hand)' },
    { code: 'SALES_ISSUE', label: 'Sales Issue (- On Hand / - Reserved)' },
    { code: 'PURCHASE_RETURN', label: 'Purchase Return (- On Hand)' },
    { code: 'SALES_RETURN', label: 'Sales Return (+ On Hand)' },
    { code: 'RESERVATION', label: 'Stock Reservation (+ Reserved)' },
    { code: 'RESERVATION_RELEASE', label: 'Release Reservation (- Reserved)' },
    { code: 'INVENTORY_ADJUSTMENT', label: 'Direct Inventory Adjustment (+/- On Hand)' }
  ];

  return createPortal(
    <div className="catalog-drawer-overlay" onClick={onClose}>
      <div className="catalog-drawer-container" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-header">
          <h2>Record Stock Transaction</h2>
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
                <label htmlFor="itemId" className="required">Product / Catalog Item</label>
                <select
                  id="itemId"
                  name="itemId"
                  value={formData.itemId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.itemId ? 'error' : ''}`}
                >
                  <option value="">-- Select Product --</option>
                  {items.filter(i => i.isInventoryTracked).map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.itemName} ({item.itemNumber})
                    </option>
                  ))}
                </select>
                {errors.itemId && <span className="error-text">{errors.itemId}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="warehouseId" className="required">Warehouse Depot</label>
                <select
                  id="warehouseId"
                  name="warehouseId"
                  value={formData.warehouseId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.warehouseId ? 'error' : ''}`}
                >
                  <option value="">-- Select Depot --</option>
                  {warehouses.map((wh) => (
                    <option key={wh.id} value={wh.id}>
                      {wh.name} ({wh.code})
                    </option>
                  ))}
                </select>
                {errors.warehouseId && <span className="error-text">{errors.warehouseId}</span>}
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="transactionType" className="required">Transaction Type</label>
              <select
                id="transactionType"
                name="transactionType"
                value={formData.transactionType}
                onChange={handleChange}
                disabled={loading}
                className={`form-control ${errors.transactionType ? 'error' : ''}`}
              >
                <option value="">-- Select Ledger Event --</option>
                {transactionTypes.map((t) => (
                  <option key={t.code} value={t.code}>
                    {t.label}
                  </option>
                ))}
              </select>
              {errors.transactionType && <span className="error-text">{errors.transactionType}</span>}
            </div>

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="quantity" className="required">Transaction Quantity</label>
                <input
                  type="number"
                  step="0.0001"
                  id="quantity"
                  name="quantity"
                  value={formData.quantity}
                  onChange={handleChange}
                  placeholder="e.g. 50"
                  disabled={loading}
                  className={`form-control ${errors.quantity ? 'error' : ''}`}
                />
                {errors.quantity && <span className="error-text">{errors.quantity}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="uomId" className="required">Transaction UOM</label>
                <select
                  id="uomId"
                  name="uomId"
                  value={formData.uomId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.uomId ? 'error' : ''}`}
                >
                  <option value="">-- Select UOM --</option>
                  {uoms.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.name} ({u.code})
                    </option>
                  ))}
                </select>
                {errors.uomId && <span className="error-text">{errors.uomId}</span>}
              </div>
            </div>

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="referenceType">Reference Doc Type</label>
                <input
                  type="text"
                  id="referenceType"
                  name="referenceType"
                  value={formData.referenceType}
                  onChange={handleChange}
                  placeholder="e.g. SALES_ORDER, ADJUSTMENT"
                  disabled={loading}
                  className="form-control"
                />
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="referenceId">Reference Document ID (UUID)</label>
                <input
                  type="text"
                  id="referenceId"
                  name="referenceId"
                  value={formData.referenceId}
                  onChange={handleChange}
                  placeholder="e.g. 9b1deb4d-3b7d..."
                  disabled={loading}
                  className={`form-control ${errors.referenceId ? 'error' : ''}`}
                />
                {errors.referenceId && <span className="error-text">{errors.referenceId}</span>}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="remarks">Ledger Remarks</label>
              <textarea
                id="remarks"
                name="remarks"
                value={formData.remarks}
                onChange={handleChange}
                placeholder="Provide comments or adjustment reasons..."
                rows={2}
                disabled={loading}
                className={`form-control ${errors.remarks ? 'error' : ''}`}
                style={{ resize: 'vertical' }}
              />
              {errors.remarks && <span className="error-text">{errors.remarks}</span>}
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
              {loading ? <div className="spinner" /> : 'Post Ledger Entry'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
