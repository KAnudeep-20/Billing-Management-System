import React from 'react';
import { Contact, Plus, ShieldAlert, Edit2, Trash2 } from 'lucide-react';

export default function ContactTable({
  contacts = [],
  onAddContactClick,
  onEditContactClick,
  onDeleteContactClick
}) {
  const hasPlaceholder = contacts.some((c) => c.isPlaceholder || c.firstName === 'Missing Information');

  return (
    <div className="section-card animate-fade-in">
      <div className="section-header">
        <h3>
          <Contact size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Contacts</span>
        </h3>
        <button 
          className="btn btn-secondary" 
          onClick={onAddContactClick}
          style={{ padding: '4px 10px', fontSize: '0.8rem' }}
        >
          <Plus size={14} />
          <span>Add Contact</span>
        </button>
      </div>

      <div style={{ padding: '0 var(--space-6) var(--space-6)' }}>
        {hasPlaceholder && (
          <div className="placeholder-warning-alert" style={{ marginTop: 'var(--space-4)' }}>
            <ShieldAlert size={16} />
            <span>Account contains a placeholder contact. Please edit the row to input valid contact details.</span>
          </div>
        )}

        <div style={{ overflowX: 'auto', marginTop: hasPlaceholder ? '0' : 'var(--space-4)' }}>
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Contact Name</th>
                <th>Role / Designation</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Type</th>
                <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {contacts.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', color: 'var(--color-text-secondary)' }}>
                    No contacts linked to this account.
                  </td>
                </tr>
              ) : (
                contacts.map((contact) => {
                  const isPlaceholder = contact.isPlaceholder || contact.firstName === 'Missing Information';
                  return (
                    <tr 
                      key={contact.id}
                      className={isPlaceholder ? 'placeholder-row' : ''}
                    >
                      <td style={{ fontWeight: 600 }}>
                        {isPlaceholder ? (
                          <span style={{ color: 'var(--color-warning)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                            <ShieldAlert size={14} />
                            Missing Information
                          </span>
                        ) : (
                          `${contact.firstName} ${contact.lastName || ''}`
                        )}
                      </td>
                      <td>
                        {isPlaceholder ? '-' : `${contact.role || ''} ${contact.designation ? '(' + contact.designation + ')' : ''}` || 'N/A'}
                      </td>
                      <td>{isPlaceholder ? '-' : contact.email || 'N/A'}</td>
                      <td>{isPlaceholder ? '-' : contact.phone || 'N/A'}</td>
                      <td>
                        {isPlaceholder ? (
                          <span className="badge badge-warning">Placeholder</span>
                        ) : contact.contactType ? (
                          <span className="badge badge-neutral">{contact.contactType.name}</span>
                        ) : (
                          <span style={{ color: 'var(--color-text-muted)' }}>-</span>
                        )}
                      </td>
                      <td className="action-cell">
                        <button
                          className="row-action-btn"
                          onClick={() => onEditContactClick && onEditContactClick(contact)}
                          title="Edit Contact"
                        >
                          <Edit2 size={14} />
                        </button>
                        <button
                          className="row-action-btn delete-btn"
                          onClick={() => onDeleteContactClick && onDeleteContactClick(contact)}
                          title="Soft Delete Contact"
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
    </div>
  );
}
