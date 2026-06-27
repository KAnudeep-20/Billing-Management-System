import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import LookupSelect from './LookupSelect';

export default function ContactFormModal({
  isOpen,
  onClose,
  onSubmit,
  editContact = null,
  lookups = {}
}) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: '',
    designation: '',
    contactTypeId: ''
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (editContact) {
        // If editing a placeholder, we blank out the "Missing Information" text
        const isPl = editContact.isPlaceholder || editContact.firstName === 'Missing Information';
        setFormData({
          firstName: isPl ? '' : editContact.firstName || '',
          lastName: editContact.lastName || '',
          email: editContact.email || '',
          phone: editContact.phone || '',
          role: editContact.role || '',
          designation: editContact.designation || '',
          contactTypeId: editContact.contactType?.id || ''
        });
      } else {
        setFormData({
          firstName: '',
          lastName: '',
          email: '',
          phone: '',
          role: '',
          designation: '',
          contactTypeId: ''
        });
      }
      setErrors({});
    }
  }, [isOpen, editContact]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};
    if (!formData.firstName?.trim()) newErrors.firstName = 'First Name is required';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setSubmitting(true);
    try {
      await onSubmit(formData, editContact?.id);
      onClose();
    } catch (err) {
      setErrors({ form: err.message || 'Failed to save contact.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" style={{ justifyContent: 'center', alignItems: 'center' }}>
      <div className="section-card" style={{ width: '90%', maxWidth: '520px', padding: 'var(--space-6)' }}>
        <div className="drawer-header" style={{ padding: '0 0 var(--space-4)', marginBottom: 'var(--space-4)' }}>
          <h3>{editContact ? 'Edit Contact Details' : 'Add Contact'}</h3>
          <button className="drawer-close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {errors.form && <div className="placeholder-warning-alert" style={{ color: 'var(--color-error)', borderColor: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)' }}>{errors.form}</div>}

          <div className="form-grid-2col">
            <div className="form-group">
              <label htmlFor="modalFirstName" className="required">First Name</label>
              <input
                type="text"
                id="modalFirstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                className={`form-control ${errors.firstName ? 'error' : ''}`}
                required
              />
              {errors.firstName && <span className="error-text">{errors.firstName}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="modalLastName">Last Name</label>
              <input
                type="text"
                id="modalLastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group form-span-2">
              <label htmlFor="modalEmail">Email</label>
              <input
                type="email"
                id="modalEmail"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="e.g. contact@email.com"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalPhone">Phone</label>
              <input
                type="text"
                id="modalPhone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <LookupSelect
                id="modalContactTypeId"
                label="Contact Type"
                name="contactTypeId"
                value={formData.contactTypeId}
                onChange={handleChange}
                options={lookups.contactTypes}
                placeholder="Choose type..."
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalRole">Role / Department</label>
              <input
                type="text"
                id="modalRole"
                name="role"
                value={formData.role}
                onChange={handleChange}
                placeholder="e.g. Finance"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalDesignation">Designation</label>
              <input
                type="text"
                id="modalDesignation"
                name="designation"
                value={formData.designation}
                onChange={handleChange}
                placeholder="e.g. Manager"
                className="form-control"
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 'var(--space-2)', borderTop: '1px solid var(--color-border)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-4)' }}>
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? <div className="spinner" /> : 'Save Contact'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
