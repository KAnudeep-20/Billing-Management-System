import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export default function ItemUomFormModal({
  isOpen,
  onClose,
  onSubmit,
  editItemUom,
  itemPrimaryUomId,
  itemPrimaryUomCode,
  uoms = [],
  existingItemUoms = []
}) {
  const [formData, setFormData] = useState({
    uomId: '',
    conversionFactor: '',
    isDefault: false
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (editItemUom) {
      setFormData({
        uomId: editItemUom.uomId || '',
        conversionFactor: editItemUom.conversionFactor ? editItemUom.conversionFactor.toString() : '',
        isDefault: editItemUom.isDefault || false
      });
    } else {
      setFormData({
        uomId: '',
        conversionFactor: '',
        isDefault: false
      });
    }
    setErrors({});
  }, [editItemUom, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.uomId) {
      newErrors.uomId = 'Unit of Measure is required.';
    } else {
      // Rule: Cannot map the item's primary UOM as a secondary mapping
      if (formData.uomId === itemPrimaryUomId) {
        newErrors.uomId = `Cannot map secondary UOM to "${itemPrimaryUomCode}" because it is already the item's Primary UOM.`;
      }
      // Rule: Cannot map the same UOM twice
      const isDuplicate = existingItemUoms.some(
        (mapping) => mapping.uomId === formData.uomId && (!editItemUom || mapping.id !== editItemUom.id)
      );
      if (isDuplicate) {
        newErrors.uomId = 'A conversion mapping already exists for this Unit of Measure.';
      }
    }

    if (!formData.conversionFactor || isNaN(formData.conversionFactor) || parseFloat(formData.conversionFactor) <= 0) {
      newErrors.conversionFactor = 'Conversion factor must be a positive number greater than 0.';
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
        uomId: formData.uomId,
        conversionFactor: parseFloat(formData.conversionFactor),
        isDefault: formData.isDefault
      };
      await onSubmit(payload, editItemUom?.id);
      onClose();
    } catch (err) {
      console.error(err);
      setErrors({ apiError: err.message || 'Operation failed. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  // Filter out the primary UOM from the list of options to prevent redundant self-conversions
  const selectableUoms = uoms.filter((u) => u.id !== itemPrimaryUomId);

  return createPortal(
    <div className="catalog-drawer-overlay" onClick={onClose}>
      <div className="catalog-drawer-container" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-header">
          <h2>{editItemUom ? 'Edit UOM Conversion' : 'Add UOM Conversion'}</h2>
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
              <label htmlFor="uomId" className="required">Secondary Unit of Measure</label>
              <select
                id="uomId"
                name="uomId"
                value={formData.uomId}
                onChange={handleChange}
                disabled={loading || !!editItemUom}
                className={`form-control ${errors.uomId ? 'error' : ''}`}
              >
                <option value="">-- Select UOM --</option>
                {selectableUoms.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.name} ({u.code})
                  </option>
                ))}
              </select>
              {errors.uomId && <span className="error-text">{errors.uomId}</span>}
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="conversionFactor" className="required">
                Conversion Factor (to {itemPrimaryUomCode})
              </label>
              <input
                type="number"
                step="0.000001"
                id="conversionFactor"
                name="conversionFactor"
                value={formData.conversionFactor}
                onChange={handleChange}
                placeholder="e.g. 12"
                disabled={loading}
                className={`form-control ${errors.conversionFactor ? 'error' : ''}`}
              />
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginTop: '4px', display: 'block' }}>
                Specifies how many units of <strong>{itemPrimaryUomCode}</strong> are in one unit of the selected UOM. (e.g. if UOM is BOX, and Primary UOM is EACH, factor is 12).
              </span>
              {errors.conversionFactor && <span className="error-text">{errors.conversionFactor}</span>}
            </div>

            <div className="form-group">
              <label className="checkbox-label" style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.9rem', marginTop: 'var(--space-4)' }}>
                <input
                  type="checkbox"
                  name="isDefault"
                  checked={formData.isDefault}
                  onChange={handleChange}
                  disabled={loading}
                />
                <span>Set as default secondary UOM</span>
              </label>
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
              {loading ? <div className="spinner" /> : editItemUom ? 'Save Conversion' : 'Add Conversion'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
