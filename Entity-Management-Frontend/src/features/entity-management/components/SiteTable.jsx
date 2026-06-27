import React from 'react';
import { MapPin, Plus, Check, Edit2, Trash2 } from 'lucide-react';

export default function SiteTable({
  sites = [],
  onAddSiteClick,
  onEditSiteClick,
  onDeleteSiteClick
}) {
  const checkUse = (site, code) => {
    return site.siteUses?.some((u) => u.code === code) || false;
  };

  return (
    <div className="section-card animate-fade-in">
      <div className="section-header">
        <h3>
          <MapPin size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Sites & Addresses</span>
        </h3>
        <button 
          className="btn btn-secondary" 
          onClick={onAddSiteClick}
          style={{ padding: '4px 10px', fontSize: '0.8rem' }}
        >
          <Plus size={14} />
          <span>Add Site</span>
        </button>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table className="enterprise-table">
          <thead>
            <tr>
              <th>Site Name</th>
              <th>Address</th>
              <th style={{ width: '80px', textAlign: 'center' }}>Bill To</th>
              <th style={{ width: '80px', textAlign: 'center' }}>Ship To</th>
              <th style={{ width: '90px', textAlign: 'center' }}>Primary</th>
              <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {sites.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', color: 'var(--color-text-secondary)' }}>
                  No sites linked to this account.
                </td>
              </tr>
            ) : (
              sites.map((site) => {
                const isPrimary = checkUse(site, 'PRIMARY');
                const isBillTo = checkUse(site, 'BILL_TO');
                const isShipTo = checkUse(site, 'SHIP_TO');
                
                return (
                  <tr key={site.id}>
                    <td style={{ fontWeight: 600 }}>{site.siteName}</td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                      {site.concatenatedAddress || `${site.addressLine1}, ${site.addressLine2 ? site.addressLine2 + ', ' : ''}${site.addressLine3 ? site.addressLine3 + ', ' : ''}${site.city}, ${site.state} ${site.postalCode}, ${site.country}`}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      {isBillTo ? (
                        <Check size={16} style={{ color: 'var(--color-success)', margin: '0 auto' }} />
                      ) : (
                        <span style={{ color: 'var(--color-text-muted)' }}>-</span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      {isShipTo ? (
                        <Check size={16} style={{ color: 'var(--color-success)', margin: '0 auto' }} />
                      ) : (
                        <span style={{ color: 'var(--color-text-muted)' }}>-</span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      {isPrimary ? (
                        <span className="badge badge-success" style={{ fontSize: '0.65rem' }}>Primary</span>
                      ) : (
                        <span style={{ color: 'var(--color-text-muted)' }}>-</span>
                      )}
                    </td>
                    <td className="action-cell">
                      <button
                        className="row-action-btn"
                        onClick={() => onEditSiteClick && onEditSiteClick(site)}
                        title="Edit Site"
                      >
                        <Edit2 size={14} />
                      </button>
                      <button
                        className="row-action-btn delete-btn"
                        onClick={() => onDeleteSiteClick && onDeleteSiteClick(site)}
                        title="Soft Delete Site"
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
