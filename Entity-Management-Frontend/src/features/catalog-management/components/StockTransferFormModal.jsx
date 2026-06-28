import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X, ArrowRightLeft } from 'lucide-react';

export default function StockTransferFormModal({
  isOpen,
  onClose,
  onSubmit,
  items = [],
  warehouses = [],
  uoms = []
}) {
  const [formData, setFormData] = useState({
    itemId: '',
    sourceWarehouseId: '',
    destinationWarehouseId: '',
    quantity: '',
    uomId: '',
    remarks: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setFormData({
      itemId: '',
      sourceWarehouseId: '',
      destinationWarehouseId: '',
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
    if (!formData.sourceWarehouseId) newErrors.sourceWarehouseId = 'Source warehouse is required.';
    if (!formData.destinationWarehouseId) newErrors.destinationWarehouseId = 'Destination warehouse is required.';

    if (formData.sourceWarehouseId && formData.destinationWarehouseId) {
      if (formData.sourceWarehouseId === formData.destinationWarehouseId) {
        newErrors.destinationWarehouseId = 'Source and Destination warehouses cannot be the same.';
      }
    }

    if (!formData.quantity || isNaN(formData.quantity) || parseFloat(formData.quantity) <= 0.0001) {
      newErrors.quantity = 'Quantity must be greater than zero.';
    }

    if (!formData.uomId) newErrors.uomId = 'Unit of Measure is required.';

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
        sourceWarehouseId: formData.sourceWarehouseId,
        destinationWarehouseId: formData.destinationWarehouseId,
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

  return createPortal(
    <div className="catalog-drawer-overlay" onClick={onClose}>
      <div className="catalog-drawer-container" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-header">
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <ArrowRightLeft size={20} style={{ color: 'var(--color-primary)' }} />
            <span>Transfer Stock Depots</span>
          </h2>
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

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="itemId" className="required">Product Item to Transfer</label>
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

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="sourceWarehouseId" className="required">Source Warehouse</label>
                <select
                  id="sourceWarehouseId"
                  name="sourceWarehouseId"
                  value={formData.sourceWarehouseId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.sourceWarehouseId ? 'error' : ''}`}
                >
                  <option value="">-- Select Source --</option>
                  {warehouses.map((wh) => (
                    <option key={wh.id} value={wh.id}>
                      {wh.name} ({wh.code})
                    </option>
                  ))}
                </select>
                {errors.sourceWarehouseId && <span className="error-text">{errors.sourceWarehouseId}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="destinationWarehouseId" className="required">Destination Warehouse</label>
                <select
                  id="destinationWarehouseId"
                  name="destinationWarehouseId"
                  value={formData.destinationWarehouseId}
                  onChange={handleChange}
                  disabled={loading}
                  className={`form-control ${errors.destinationWarehouseId ? 'error' : ''}`}
                >
                  <option value="">-- Select Destination --</option>
                  {warehouses.map((wh) => (
                    <option key={wh.id} value={wh.id}>
                      {wh.name} ({wh.code})
                    </option>
                  ))}
                </select>
                {errors.destinationWarehouseId && <span className="error-text">{errors.destinationWarehouseId}</span>}
              </div>
            </div>

            <div className="grid-two-columns">
              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="quantity" className="required">Quantity to Transfer</label>
                <input
                  type="number"
                  step="0.0001"
                  id="quantity"
                  name="quantity"
                  value={formData.quantity}
                  onChange={handleChange}
                  placeholder="e.g. 10"
                  disabled={loading}
                  className={`form-control ${errors.quantity ? 'error' : ''}`}
                />
                {errors.quantity && <span className="error-text">{errors.quantity}</span>}
              </div>

              <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
                <label htmlFor="uomId" className="required">Unit of Measure</label>
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

            <div className="form-group">
              <label htmlFor="remarks">Transfer Comments</label>
              <textarea
                id="remarks"
                name="remarks"
                value={formData.remarks}
                onChange={handleChange}
                placeholder="e.g. Re-stocking retail branch..."
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
              {loading ? <div className="spinner" /> : 'Confirm Stock Transfer'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
