import React, { useState, useEffect } from 'react';
import { X, Check } from 'lucide-react';
import LookupSelect from './LookupSelect';

const INITIAL_FORM_STATE = {
  // Step 1 & 2: Entity
  entityCategory: 'ORGANIZATION',
  entityTypeCodes: [],
  organizationName: '',
  tin: '',
  fullName: '',
  identificationType: '',
  identificationNumber: '',
  
  // Step 3: Account
  accountName: '',
  natureOfBusiness: '',
  creditLimit: '',
  paymentTermId: '',
  billingCycleId: '',
  creditClassification: '',
  creditRisk: '',

  // Step 4: Site
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
  isPrimary: true,

  // Step 5: Contact
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  role: '',
  designation: '',
  contactTypeId: ''
};

export default function MultiStepForm({
  isOpen,
  onClose,
  onSubmit,
  editEntity = null, // If editing, we pass the entity to edit
  lookups = {},
  lookupsLoading = false
}) {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const isEditMode = !!editEntity;

  useEffect(() => {
    if (isOpen) {
      if (editEntity) {
        // Initialize editing state
        setFormData({
          ...INITIAL_FORM_STATE,
          entityCategory: editEntity.entityCategory || 'ORGANIZATION',
          entityTypeCodes: editEntity.entityTypeCodes || [],
          organizationName: editEntity.organizationDetails?.organizationName || '',
          tin: editEntity.organizationDetails?.tin || '',
          fullName: editEntity.personDetails?.fullName || '',
          identificationType: editEntity.personDetails?.identificationType || '',
          identificationNumber: editEntity.personDetails?.identificationNumber || ''
        });
        setStep(1); // Editing only updates Steps 1 & 2 (Entity Profile)
      } else {
        setFormData(INITIAL_FORM_STATE);
        setStep(1);
      }
      setErrors({});
    }
  }, [isOpen, editEntity]);

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

  const handleTypeCheckboxChange = (code) => {
    setFormData((prev) => {
      const currentCodes = prev.entityTypeCodes;
      const newCodes = currentCodes.includes(code)
        ? currentCodes.filter((c) => c !== code)
        : [...currentCodes, code];
      
      if (errors.entityTypeCodes) {
        setErrors((err) => ({ ...err, entityTypeCodes: '' }));
      }
      return { ...prev, entityTypeCodes: newCodes };
    });
  };

  const validateStep = () => {
    const newErrors = {};
    if (step === 1) {
      if (!formData.entityCategory) newErrors.entityCategory = 'Entity Category is required';
      if (!formData.entityTypeCodes || formData.entityTypeCodes.length === 0) {
        newErrors.entityTypeCodes = 'Select at least one entity type/role';
      }
    }
    
    if (step === 2) {
      if (formData.entityCategory === 'ORGANIZATION') {
        if (!formData.organizationName?.trim()) newErrors.organizationName = 'Organization Name is required';
        if (!formData.tin?.trim()) newErrors.tin = 'TIN Number is required';
      } else {
        if (!formData.fullName?.trim()) newErrors.fullName = 'Full Name is required';
        if (!formData.identificationType) newErrors.identificationType = 'Identification Type is required';
        if (!formData.identificationNumber?.trim()) newErrors.identificationNumber = 'Identification Number is required';
      }
    }

    if (step === 3 && !isEditMode) {
      if (!formData.accountName?.trim()) newErrors.accountName = 'Account Name is required';
      if (!formData.paymentTermId) newErrors.paymentTermId = 'Payment Term is required';
    }

    if (step === 4 && !isEditMode) {
      if (!formData.siteName?.trim()) newErrors.siteName = 'Site Name is required';
      if (!formData.addressLine1?.trim()) newErrors.addressLine1 = 'Address Line 1 is required';
      if (!formData.isPrimary) newErrors.isPrimary = 'At least one primary site is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNext = () => {
    if (validateStep()) {
      setStep((s) => s + 1);
    }
  };

  const handleBack = () => {
    setStep((s) => s - 1);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!validateStep()) return;

    setSubmitting(true);
    try {
      await onSubmit(formData, isEditMode ? editEntity.id : null);
      onClose();
    } catch (err) {
      console.error(err);
      if (err.validationErrors) {
        // Flatten inline backend errors
        const mappedErrors = {};
        Object.entries(err.validationErrors).forEach(([path, msg]) => {
          // e.g. organizationDetails.tin -> tin
          const field = path.includes('.') ? path.split('.').pop() : path;
          mappedErrors[field] = msg;
        });
        setErrors(mappedErrors);
      } else {
        setErrors({ form: err.message || 'Operation failed.' });
      }
    } finally {
      setSubmitting(false);
    }
  };

  // Steps Configuration
  const steps = isEditMode 
    ? [{ num: 1, name: 'Profile' }, { num: 2, name: 'Identity' }]
    : [
        { num: 1, name: 'Profile' },
        { num: 2, name: 'Identity' },
        { num: 3, name: 'Account' },
        { num: 4, name: 'Site' },
        { num: 5, name: 'Contact' }
      ];

  return (
    <div className="modal-overlay">
      <div className="drawer-container">
        {/* Header */}
        <div className="drawer-header">
          <h2>{isEditMode ? 'Edit Customer Entity' : 'Create Customer Entity'}</h2>
          <button className="drawer-close-btn" onClick={onClose}>
            <X size={24} />
          </button>
        </div>

        {/* Steps Indicators */}
        <div className="wizard-steps">
          {steps.map((st) => (
            <React.Fragment key={st.num}>
              <div className={`step-indicator ${step === st.num ? 'active' : ''} ${step > st.num ? 'completed' : ''}`}>
                <span className="step-dot">{step > st.num ? <Check size={10} /> : st.num}</span>
                <span>{st.name}</span>
              </div>
              {st.num < steps.length && <span className="step-chevron">/</span>}
            </React.Fragment>
          ))}
        </div>

        {/* Form Body */}
        <form onSubmit={step === steps.length ? handleFormSubmit : (e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', flex: 1, height: '100%' }}>
          <div className="drawer-body">
            {errors.form && <div className="placeholder-warning-alert" style={{ color: 'var(--color-error)', borderColor: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)' }}>{errors.form}</div>}

            {/* STEP 1: Basic Profile */}
            {step === 1 && (
              <div className="animate-fade-in">
                <h3 className="form-section-title">Entity Category & Types</h3>
                
                <div className="form-grid-2col">
                  <div className="form-group form-span-2">
                    <label className="required">Entity Category</label>
                    <div style={{ display: 'flex', gap: 'var(--space-4)', marginTop: 'var(--space-2)' }}>
                      <label className="checkbox-label">
                        <input
                          type="radio"
                          name="entityCategory"
                          value="ORGANIZATION"
                          checked={formData.entityCategory === 'ORGANIZATION'}
                          onChange={handleChange}
                        />
                        <span>Organization (Company)</span>
                      </label>
                      <label className="checkbox-label">
                        <input
                          type="radio"
                          name="entityCategory"
                          value="PERSON"
                          checked={formData.entityCategory === 'PERSON'}
                          onChange={handleChange}
                        />
                        <span>Person (Individual)</span>
                      </label>
                    </div>
                  </div>

                  <div className="form-group form-span-2">
                    <label className="required">Entity Roles / Types</label>
                    {lookupsLoading ? (
                      <div className="skeleton" style={{ height: '40px' }} />
                    ) : (
                      <div className="checkbox-grid">
                        {lookups.entityTypes?.map((type) => (
                          <label key={type.id} className="checkbox-label">
                            <input
                              type="checkbox"
                              checked={formData.entityTypeCodes.includes(type.code)}
                              onChange={() => handleTypeCheckboxChange(type.code)}
                            />
                            <span>{type.name}</span>
                          </label>
                        ))}
                      </div>
                    )}
                    {errors.entityTypeCodes && <span className="error-text">{errors.entityTypeCodes}</span>}
                  </div>
                </div>
              </div>
            )}

            {/* STEP 2: Identity Details (Conditional) */}
            {step === 2 && (
              <div className="animate-fade-in">
                {formData.entityCategory === 'ORGANIZATION' ? (
                  <div>
                    <h3 className="form-section-title">Organization Info</h3>
                    <div className="form-grid-2col">
                      <div className="form-group form-span-2">
                        <label htmlFor="organizationName" className="required">Legal Business Name</label>
                        <input
                          type="text"
                          id="organizationName"
                          name="organizationName"
                          value={formData.organizationName}
                          onChange={handleChange}
                          className={`form-control ${errors.organizationName ? 'error' : ''}`}
                        />
                        {errors.organizationName && <span className="error-text">{errors.organizationName}</span>}
                      </div>

                      <div className="form-group form-span-2">
                        <label htmlFor="tin" className="required">Tax Identification Number (TIN / GSTIN)</label>
                        <input
                          type="text"
                          id="tin"
                          name="tin"
                          value={formData.tin}
                          onChange={handleChange}
                          className={`form-control ${errors.tin ? 'error' : ''}`}
                          placeholder="e.g. GSTIN 22AAAAA0000A1Z5"
                        />
                        {errors.tin && <span className="error-text">{errors.tin}</span>}
                      </div>
                    </div>
                  </div>
                ) : (
                  <div>
                    <h3 className="form-section-title">Personal Info</h3>
                    <div className="form-grid-2col">
                      <div className="form-group form-span-2">
                        <label htmlFor="fullName" className="required">Full Legal Name</label>
                        <input
                          type="text"
                          id="fullName"
                          name="fullName"
                          value={formData.fullName}
                          onChange={handleChange}
                          className={`form-control ${errors.fullName ? 'error' : ''}`}
                        />
                        {errors.fullName && <span className="error-text">{errors.fullName}</span>}
                      </div>

                      <div className="form-group">
                        <label htmlFor="identificationType" className="required">ID Document Type</label>
                        <select
                          id="identificationType"
                          name="identificationType"
                          value={formData.identificationType}
                          onChange={handleChange}
                          className={`form-control ${errors.identificationType ? 'error' : ''}`}
                        >
                          <option value="">Select doc type...</option>
                          <option value="PAN">PAN Card</option>
                          <option value="Aadhar">Aadhar Card</option>
                          <option value="Driver Licence">Driver's License</option>
                          <option value="Passport">Passport</option>
                        </select>
                        {errors.identificationType && <span className="error-text">{errors.identificationType}</span>}
                      </div>

                      <div className="form-group">
                        <label htmlFor="identificationNumber" className="required">Identification Number</label>
                        <input
                          type="text"
                          id="identificationNumber"
                          name="identificationNumber"
                          value={formData.identificationNumber}
                          onChange={handleChange}
                          className={`form-control ${errors.identificationNumber ? 'error' : ''}`}
                        />
                        {errors.identificationNumber && <span className="error-text">{errors.identificationNumber}</span>}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* STEP 3: Initial Account Details */}
            {step === 3 && (
              <div className="animate-fade-in">
                <h3 className="form-section-title">First Account setup</h3>
                <div className="form-grid-2col">
                  <div className="form-group form-span-2">
                    <label htmlFor="accountName" className="required">Account Name</label>
                    <input
                      type="text"
                      id="accountName"
                      name="accountName"
                      value={formData.accountName}
                      onChange={handleChange}
                      placeholder="e.g. Primary Commercial Account"
                      className={`form-control ${errors.accountName ? 'error' : ''}`}
                    />
                    {errors.accountName && <span className="error-text">{errors.accountName}</span>}
                  </div>

                  <div className="form-group">
                    <label htmlFor="natureOfBusiness">Nature of Business</label>
                    <input
                      type="text"
                      id="natureOfBusiness"
                      name="natureOfBusiness"
                      value={formData.natureOfBusiness}
                      onChange={handleChange}
                      placeholder="e.g. Retail / IT Services"
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="creditLimit">Credit Limit ($)</label>
                    <input
                      type="number"
                      id="creditLimit"
                      name="creditLimit"
                      value={formData.creditLimit}
                      onChange={handleChange}
                      placeholder="e.g. 50000"
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <LookupSelect
                      id="paymentTermId"
                      label="Payment Terms"
                      name="paymentTermId"
                      value={formData.paymentTermId}
                      onChange={handleChange}
                      options={lookups.paymentTerms}
                      placeholder="Select terms..."
                      required
                      error={errors.paymentTermId}
                    />
                  </div>

                  <div className="form-group">
                    <LookupSelect
                      id="billingCycleId"
                      label="Billing Cycle"
                      name="billingCycleId"
                      value={formData.billingCycleId}
                      onChange={handleChange}
                      options={lookups.billingCycles}
                      placeholder="Select billing cycle..."
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="creditRisk">Credit Risk Rating</label>
                    <select
                      id="creditRisk"
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
                    <label htmlFor="creditClassification">Credit Classification</label>
                    <select
                      id="creditClassification"
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
              </div>
            )}

            {/* STEP 4: Initial Site Details */}
            {step === 4 && (
              <div className="animate-fade-in">
                <h3 className="form-section-title">First Address / Site Use</h3>
                <div className="form-grid-2col">
                  <div className="form-group form-span-2">
                    <label htmlFor="siteName" className="required">Site Name</label>
                    <input
                      type="text"
                      id="siteName"
                      name="siteName"
                      value={formData.siteName}
                      onChange={handleChange}
                      placeholder="e.g. Headquarters / Shipping Hub"
                      className={`form-control ${errors.siteName ? 'error' : ''}`}
                    />
                    {errors.siteName && <span className="error-text">{errors.siteName}</span>}
                  </div>

                  <div className="form-group form-span-2">
                    <label htmlFor="addressLine1" className="required">Address Line 1</label>
                    <input
                      type="text"
                      id="addressLine1"
                      name="addressLine1"
                      value={formData.addressLine1}
                      onChange={handleChange}
                      className={`form-control ${errors.addressLine1 ? 'error' : ''}`}
                    />
                    {errors.addressLine1 && <span className="error-text">{errors.addressLine1}</span>}
                  </div>

                  <div className="form-group">
                    <label htmlFor="addressLine2">Address Line 2</label>
                    <input
                      type="text"
                      id="addressLine2"
                      name="addressLine2"
                      value={formData.addressLine2}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="addressLine3">Address Line 3</label>
                    <input
                      type="text"
                      id="addressLine3"
                      name="addressLine3"
                      value={formData.addressLine3}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="city">City</label>
                    <input
                      type="text"
                      id="city"
                      name="city"
                      value={formData.city}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="state">State</label>
                    <input
                      type="text"
                      id="state"
                      name="state"
                      value={formData.state}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="postalCode">Postal Code</label>
                    <input
                      type="text"
                      id="postalCode"
                      name="postalCode"
                      value={formData.postalCode}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="country">Country</label>
                    <input
                      type="text"
                      id="country"
                      name="country"
                      value={formData.country}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group form-span-2">
                    <label>Site Purpose / Uses</label>
                    <div style={{ display: 'flex', gap: 'var(--space-6)', marginTop: 'var(--space-2)' }}>
                      <label className="checkbox-label">
                        <input
                          type="checkbox"
                          name="isBillTo"
                          checked={formData.isBillTo}
                          onChange={handleChange}
                        />
                        <span>Bill To Address</span>
                      </label>

                      <label className="checkbox-label">
                        <input
                          type="checkbox"
                          name="isShipTo"
                          checked={formData.isShipTo}
                          onChange={handleChange}
                        />
                        <span>Ship To Address</span>
                      </label>

                      <label className="checkbox-label" title="Every account must have one primary site">
                        <input
                          type="checkbox"
                          name="isPrimary"
                          disabled // Always primary for the initial site
                          checked={formData.isPrimary}
                          onChange={handleChange}
                        />
                        <span style={{ fontWeight: 600, color: 'var(--color-primary)' }}>Primary Site (Mandatory)</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* STEP 5: Initial Contact Details */}
            {step === 5 && (
              <div className="animate-fade-in">
                <h3 className="form-section-title">First Point of Contact</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginBottom: 'var(--space-4)' }}>
                  If left blank, a placeholder point of contact named <strong>"Missing Information"</strong> will be auto-created for follow-up.
                </p>

                <div className="form-grid-2col">
                  <div className="form-group">
                    <label htmlFor="firstName">First Name</label>
                    <input
                      type="text"
                      id="firstName"
                      name="firstName"
                      value={formData.firstName}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="lastName">Last Name</label>
                    <input
                      type="text"
                      id="lastName"
                      name="lastName"
                      value={formData.lastName}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      placeholder="e.g. contact@company.com"
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="phone">Phone Number</label>
                    <input
                      type="text"
                      id="phone"
                      name="phone"
                      value={formData.phone}
                      onChange={handleChange}
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="role">Role / Relationship</label>
                    <input
                      type="text"
                      id="role"
                      name="role"
                      value={formData.role}
                      onChange={handleChange}
                      placeholder="e.g. CFO / Procurement lead"
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="designation">Designation</label>
                    <input
                      type="text"
                      id="designation"
                      name="designation"
                      value={formData.designation}
                      onChange={handleChange}
                      placeholder="e.g. Director"
                      className="form-control"
                    />
                  </div>

                  <div className="form-group">
                    <LookupSelect
                      id="contactTypeId"
                      label="Contact Type"
                      name="contactTypeId"
                      value={formData.contactTypeId}
                      onChange={handleChange}
                      options={lookups.contactTypes}
                      placeholder="Select contact type..."
                    />
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Footer controls */}
          <div className="drawer-footer">
            {step > 1 ? (
              <button type="button" className="btn btn-secondary" onClick={handleBack} disabled={submitting}>
                Back
              </button>
            ) : (
              <div /> // Spacer
            )}

            {step < steps.length ? (
              <button type="button" className="btn btn-primary" onClick={handleNext}>
                Next
              </button>
            ) : (
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? <div className="spinner" /> : 'Save Details'}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
