import React from 'react';
import { Layers, Plus, Edit2, Trash2 } from 'lucide-react';

export default function AccountTable({
  accounts = [],
  selectedAccountId = null,
  onAccountSelect,
  onAddAccountClick,
  onEditAccountClick,
  onDeleteAccountClick
}) {
  return (
    <div className="section-card animate-fade-in">
      <div className="section-header">
        <h3>
          <Layers size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Accounts</span>
        </h3>
        <button 
          className="btn btn-secondary" 
          onClick={onAddAccountClick}
          style={{ padding: '4px 10px', fontSize: '0.8rem' }}
        >
          <Plus size={14} />
          <span>Add Account</span>
        </button>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table className="enterprise-table">
          <thead>
            <tr>
              <th style={{ width: '40px' }}>Select</th>
              <th>Account Name</th>
              <th>Nature of Business</th>
              <th>Status</th>
              <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {accounts.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', color: 'var(--color-text-secondary)' }}>
                  No accounts linked to this entity.
                </td>
              </tr>
            ) : (
              accounts.map((acct) => {
                const isSelected = acct.id === selectedAccountId;
                return (
                  <tr
                    key={acct.id}
                    onClick={() => onAccountSelect && onAccountSelect(acct)}
                    className={`clickable-row ${isSelected ? 'selected' : ''}`}
                  >
                    <td>
                      <input
                        type="radio"
                        checked={isSelected}
                        onChange={() => onAccountSelect && onAccountSelect(acct)}
                        style={{ cursor: 'pointer', accentColor: 'var(--color-primary)' }}
                      />
                    </td>
                    <td style={{ fontWeight: 600 }}>{acct.accountName}</td>
                    <td>{acct.natureOfBusiness || 'N/A'}</td>
                    <td>
                      <span className={`badge ${
                        acct.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'
                      }`}>
                        {acct.status}
                      </span>
                    </td>
                    <td className="action-cell" onClick={(e) => e.stopPropagation()}>
                      <button
                        className="row-action-btn"
                        onClick={() => onEditAccountClick && onEditAccountClick(acct)}
                        title="Edit Account"
                      >
                        <Edit2 size={14} />
                      </button>
                      <button
                        className="row-action-btn delete-btn"
                        onClick={() => onDeleteAccountClick && onDeleteAccountClick(acct)}
                        title="Soft Delete Account"
                      >
                        <Trash2 size={14} />
                      </button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
