import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import LookupSelect from './LookupSelect';

export default function AccountFormModal({
  isOpen,
  onClose,
  onSubmit,
  editAccount = null,
  lookups = {}
}) {
  const [formData, setFormData] = useState({
    accountName: '',
    natureOfBusiness: '',
    creditLimit: '',
    paymentTermId: '',
    billingCycleId: '',
    creditClassification: '',
    creditRisk: ''
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (editAccount) {
        setFormData({
          accountName: editAccount.accountName || '',
          natureOfBusiness: editAccount.natureOfBusiness || '',
          creditLimit: editAccount.creditLimit || '',
          paymentTermId: editAccount.paymentTerm?.id || '',
          billingCycleId: editAccount.billingCycle?.id || '',
          creditClassification: editAccount.creditClassification || '',
          creditRisk: editAccount.creditRisk || ''
        });
      } else {
        setFormData({
          accountName: '',
          natureOfBusiness: '',
          creditLimit: '',
          paymentTermId: '',
          billingCycleId: '',
          creditClassification: '',
          creditRisk: ''
        });
      }
      setErrors({});
    }
  }, [isOpen, editAccount]);

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
    if (!formData.accountName?.trim()) newErrors.accountName = 'Account Name is required';
    if (!formData.paymentTermId) newErrors.paymentTermId = 'Payment Term is required';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setSubmitting(true);
    try {
      await onSubmit(formData, editAccount?.id);
      onClose();
    } catch (err) {
      setErrors({ form: err.message || 'Failed to save account.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" style={{ justifyContent: 'center', alignItems: 'center' }}>
      <div className="section-card" style={{ width: '90%', maxWidth: '560px', padding: 'var(--space-6)' }}>
        <div className="drawer-header" style={{ padding: '0 0 var(--space-4)', marginBottom: 'var(--space-4)' }}>
          <h3>{editAccount ? 'Edit Account' : 'Add Account'}</h3>
          <button className="drawer-close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {errors.form && <div className="placeholder-warning-alert" style={{ color: 'var(--color-error)', borderColor: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)' }}>{errors.form}</div>}

          <div className="form-grid-2col">
            <div className="form-group form-span-2">
              <label htmlFor="modalAccountName" className="required">Account Name</label>
              <input
                type="text"
                id="modalAccountName"
                name="accountName"
                value={formData.accountName}
                onChange={handleChange}
                className={`form-control ${errors.accountName ? 'error' : ''}`}
                required
              />
              {errors.accountName && <span className="error-text">{errors.accountName}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="modalNatureOfBusiness">Nature of Business</label>
              <input
                type="text"
                id="modalNatureOfBusiness"
                name="natureOfBusiness"
                value={formData.natureOfBusiness}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalCreditLimit">Credit Limit ($)</label>
              <input
                type="number"
                id="modalCreditLimit"
                name="creditLimit"
                value={formData.creditLimit}
                onChange={handleChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <LookupSelect
                id="modalPaymentTermId"
                label="Payment Terms"
                name="paymentTermId"
                value={formData.paymentTermId}
                onChange={handleChange}
                options={lookups.paymentTerms}
                required
                error={errors.paymentTermId}
              />
            </div>

            <div className="form-group">
              <LookupSelect
                id="modalBillingCycleId"
                label="Billing Cycle"
                name="billingCycleId"
                value={formData.billingCycleId}
                onChange={handleChange}
                options={lookups.billingCycles}
              />
            </div>

            <div className="form-group">
              <label htmlFor="modalCreditRisk">Credit Risk Rating</label>
              <select
                id="modalCreditRisk"
                name="creditRisk"
                value={formData.creditRisk}
                onChange={handleChange}
                className="form-control"
              >
                <option value="">Choose risk...</option>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="modalCreditClassification">Credit Classification</label>
              <select
                id="modalCreditClassification"
                name="creditClassification"
                value={formData.creditClassification}
                onChange={handleChange}
                className="form-control"
              >
                <option value="">Choose class...</option>
                <option value="GOLD">Gold</option>
                <option value="SILVER">Silver</option>
                <option value="BRONZE">Bronze</option>
              </select>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 'var(--space-2)', borderTop: '1px solid var(--color-border)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-4)' }}>
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? <div className="spinner" /> : 'Save Account'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
