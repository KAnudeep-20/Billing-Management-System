import React from 'react';
import { Building2, User, CreditCard, ShieldAlert, DollarSign } from 'lucide-react';

export default function EntitySummaryCard({
  entity = null,
  selectedAccount = null
}) {
  if (!entity) return null;

  const isOrg = entity.entityCategory === 'ORGANIZATION';
  const name = isOrg
    ? entity.organizationDetails?.organizationName || 'N/A'
    : entity.personDetails?.fullName || 'N/A';

  // Format credit limit
  const formatCurrency = (val) => {
    if (val === undefined || val === null) return 'N/A';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(val);
  };

  return (
    <div className="summary-panel animate-fade-in">
      <div className="summary-left">
        <div className="summary-avatar">
          {isOrg ? <Building2 size={24} /> : <User size={24} />}
        </div>
        <div className="summary-title">
          <h2>{name}</h2>
          <div className="summary-types">
            <span className={`badge ${isOrg ? 'badge-primary' : 'badge-neutral'}`}>
              {entity.entityCategory}
            </span>
            {entity.entityTypeCodes?.map((code) => (
              <span key={code} className="badge badge-primary">
                {code}
              </span>
            ))}
          </div>
        </div>
      </div>

      <div className="summary-meta-grid">
        {/* Category specific details */}
        {isOrg ? (
          <div className="summary-meta-item">
            <span className="summary-meta-label">TIN Number</span>
            <span className="summary-meta-value">{entity.organizationDetails?.tin || 'N/A'}</span>
          </div>
        ) : (
          <>
            <div className="summary-meta-item">
              <span className="summary-meta-label">Identity Doc</span>
              <span className="summary-meta-value">{entity.personDetails?.identificationType || 'N/A'}</span>
            </div>
            <div className="summary-meta-item">
              <span className="summary-meta-label">ID Number</span>
              <span className="summary-meta-value">{entity.personDetails?.identificationNumber || 'N/A'}</span>
            </div>
          </>
        )}

        {/* Selected Account specific details */}
        <div className="summary-meta-item">
          <span className="summary-meta-label">Selected Account Payment Terms</span>
          <span className="summary-meta-value" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <CreditCard size={14} style={{ color: 'var(--color-primary)' }} />
            {selectedAccount?.paymentTerm?.name || 'N/A'}
          </span>
        </div>

        <div className="summary-meta-item">
          <span className="summary-meta-label">Credit limit</span>
          <span className="summary-meta-value" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <DollarSign size={14} style={{ color: 'var(--color-success)' }} />
            {formatCurrency(selectedAccount?.creditLimit)}
          </span>
        </div>

        <div className="summary-meta-item">
          <span className="summary-meta-label">Credit Profile</span>
          <span className="summary-meta-value" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {selectedAccount?.creditRisk ? (
              <span className={`badge ${
                selectedAccount.creditRisk.toUpperCase() === 'LOW' ? 'badge-success' :
                selectedAccount.creditRisk.toUpperCase() === 'MEDIUM' ? 'badge-warning' : 'badge-danger'
              }`}>
                {selectedAccount.creditRisk} Risk
              </span>
            ) : null}
            {selectedAccount?.creditClassification ? (
              <span className="badge badge-primary">
                {selectedAccount.creditClassification}
              </span>
            ) : null}
            {!selectedAccount?.creditRisk && !selectedAccount?.creditClassification && 'N/A'}
          </span>
        </div>
      </div>
    </div>
  );
}
