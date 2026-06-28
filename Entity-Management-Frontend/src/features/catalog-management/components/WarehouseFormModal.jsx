import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export default function WarehouseFormModal({
  isOpen,
  onClose,
  onSubmit,
  editWarehouse
}) {
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    address: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (editWarehouse) {
      setFormData({
        code: editWarehouse.code || '',
        name: editWarehouse.name || '',
        address: editWarehouse.address || ''
      });
    } else {
      setFormData({
        code: '',
        name: '',
        address: ''
      });
    }
    setErrors({});
  }, [editWarehouse, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.code.trim()) newErrors.code = 'Warehouse code is required.';
    else if (formData.code.length > 50) newErrors.code = 'Warehouse code must not exceed 50 characters.';

    if (!formData.name.trim()) newErrors.name = 'Warehouse name is required.';
    else if (formData.name.length > 100) newErrors.name = 'Warehouse name must not exceed 100 characters.';

    if (formData.address && formData.address.length > 255) {
      newErrors.address = 'Address must not exceed 255 characters.';
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
        code: formData.code.trim().toUpperCase(),
        name: formData.name.trim(),
        address: formData.address.trim() || null
      };
      await onSubmit(payload, editWarehouse?.id);
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
          <h2>{editWarehouse ? 'Edit Warehouse Depot' : 'Add Warehouse Depot'}</h2>
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
              <label htmlFor="code" className="required">Warehouse Code</label>
              <input
                type="text"
                id="code"
                name="code"
                value={formData.code}
                onChange={handleChange}
                placeholder="e.g. WH-NY-01"
                disabled={loading}
                className={`form-control ${errors.code ? 'error' : ''}`}
              />
              {errors.code && <span className="error-text">{errors.code}</span>}
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="name" className="required">Warehouse Name</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="e.g. New York Logistics Hub"
                disabled={loading}
                className={`form-control ${errors.name ? 'error' : ''}`}
              />
              {errors.name && <span className="error-text">{errors.name}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="address">Address Location</label>
              <textarea
                id="address"
                name="address"
                value={formData.address}
                onChange={handleChange}
                placeholder="Provide depot shipping address..."
                rows={3}
                disabled={loading}
                className={`form-control ${errors.address ? 'error' : ''}`}
                style={{ resize: 'vertical' }}
              />
              {errors.address && <span className="error-text">{errors.address}</span>}
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
              {loading ? <div className="spinner" /> : editWarehouse ? 'Save Changes' : 'Create Warehouse'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
