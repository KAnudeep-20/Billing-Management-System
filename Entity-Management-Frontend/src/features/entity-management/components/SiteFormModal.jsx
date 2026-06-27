import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';

export default function SiteFormModal({
  isOpen,
  onClose,
  onSubmit,
  editSite = null,
  lookups = {}
}) {
  const [formData, setFormData] = useState({
    siteName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    city: '',
    state: '',
    postalCode: '',
    country: '',
    isBillTo: false,
    isShipTo: false,
    isPrimary: false
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (editSite) {
        const hasUse = (code) => editSite.siteUses?.some((u) => u.code === code) || false;
        setFormData({
          siteName: editSite.siteName || '',
          addressLine1: editSite.addressLine1 || '',
          addressLine2: editSite.addressLine2 || '',
          addressLine3: editSite.addressLine3 || '',
          city: editSite.city || '',
          state: editSite.state || '',
          postalCode: editSite.postalCode || '',
          country: editSite.country || '',
          isBillTo: hasUse('BILL_TO'),
          isShipTo: hasUse('SHIP_TO'),
          isPrimary: hasUse('PRIMARY')
        });
      } else {
        setFormData({
          siteName: '',
          addressLine1: '',
          addressLine2: '',
          addressLine3: '',
          city: '',
          state: '',
          postalCode: '',
          country: '',
          isBillTo: true,
          isShipTo: true,
          isPrimary: false
        });
      }
      setErrors({});
    }
  }, [isOpen, editSite]);

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

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};
    if (!formData.siteName?.trim()) newErrors.siteName = 'Site Name is required';
    if (!formData.addressLine1?.trim()) newErrors.addressLine1 = 'Address Line 1 is required';
    if (!formData.isBillTo && !formData.isShipTo && !formData.isPrimary) {
      newErrors.uses = 'At least one purpose/use must be selected';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setSubmitting(true);
    try {
      // Map true checkboxes to their lookup IDs
      const selectedSiteUseIds = [];
      const primaryUse = lookups.siteUses?.find((u) => u.code === 'PRIMARY');
      const billToUse = lookups.siteUses?.find((u) => u.code === 'BILL_TO');
      const shipToUse = lookups.siteUses?.find((u) => u.code === 'SHIP_TO');

      if (formData.isPrimary && primaryUse) selectedSiteUseIds.push(primaryUse.id);
      if (formData.isBillTo && billToUse) selectedSiteUseIds.push(billToUse.id);
      if (formData.isShipTo && shipToUse) selectedSiteUseIds.push(shipToUse.id);

      await onSubmit({
        ...formData,
        siteUseIds: selectedSiteUseIds
      }, editSite?.id);
      onClose();
    } catch (err) {
      setErrors({ form: err.message || 'Failed to save address.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" style={{ justifyContent: 'center', alignItems: 'center' }}>
      <div className="section-card" style={{ width: '90%', maxWidth: '580px', padding: 'var(--space-6)' }}>
        <div className="drawer-header" style={{ padding: '0 0 var(--space-4)', marginBottom: 'var(--space-4)' }}>
          <h3>{editSite ? 'Edit Site Address' : 'Add Site Address'}</h3>
          <button className="drawer-close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {errors.form && <div className="placeholder-warning-alert" style={{ color: 'var(--color-error)', borderColor: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)' }}>{errors.form}</div>}

          <div className="form-grid-2col">
            <div className="form-group form-span-2">
              <label htmlFor="modalSiteName" className="required">Site Name</label>
              <input
                type="text"
                id="modalSiteName"
                name="siteName"
                value={formData.siteName}
                onChange={handleChange}
                placeholder="e.g. Warehouse A / Head Office"
                className={`form-control ${errors.siteName ? 'error' : ''}`}
                required
              />
              {errors.siteName && <span className="error-text">{errors.siteName}</span>}
            </div>

            <div className="form-group form-span-2">
              <label htmlFor="modalAddressLine1" className="required">Address Line 1</label>
              <input
                type="text"
                id="modalAddressLine1"
                name="addressLine1"
                value={formData.addressLine1}
                onChange={handleChange}
                className={`form-control ${errors.addressLine1 ? 'error' : ''}`}
                required
              />
              {errors.addressLine1 && <span className="error-text">{errors.addressLine1}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="modalAddressLine2">Address Line 2</label>
              <input
                type="text"
                id="modalAddressLine2"
                name="addressLine2"
                value={formData.addressLine2}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalAddressLine3">Address Line 3</label>
              <input
                type="text"
                id="modalAddressLine3"
                name="addressLine3"
                value={formData.addressLine3}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalCity">City</label>
              <input
                type="text"
                id="modalCity"
                name="city"
                value={formData.city}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalState">State</label>
              <input
                type="text"
                id="modalState"
                name="state"
                value={formData.state}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalPostalCode">Postal Code</label>
              <input
                type="text"
                id="modalPostalCode"
                name="postalCode"
                value={formData.postalCode}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalCountry">Country</label>
              <input
                type="text"
                id="modalCountry"
                name="country"
                value={formData.country}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group form-span-2">
              <label>Site Purpose / Uses</label>
              <div style={{ display: 'flex', gap: 'var(--space-4)', marginTop: 'var(--space-2)' }}>
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isBillTo"
                    checked={formData.isBillTo}
                    onChange={handleChange}
                  />
                  <span>Bill To</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isShipTo"
                    checked={formData.isShipTo}
                    onChange={handleChange}
                  />
                  <span>Ship To</span>
                </label>

                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isPrimary"
                    checked={formData.isPrimary}
                    onChange={handleChange}
                  />
                  <span style={{ fontWeight: 600, color: 'var(--color-primary)' }}>Primary</span>
                </label>
              </div>
              {errors.uses && <span className="error-text" style={{ display: 'block', marginTop: '4px' }}>{errors.uses}</span>}
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 'var(--space-2)', borderTop: '1px solid var(--color-border)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-4)' }}>
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? <div className="spinner" /> : 'Save Address'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
