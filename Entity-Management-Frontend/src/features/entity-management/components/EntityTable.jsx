import React from 'react';
import { Edit2, Trash2, Building2, User, ChevronLeft, ChevronRight, Inbox } from 'lucide-react';

export default function EntityTable({
  entities = [],
  loading = false,
  selectedEntityId = null,
  onRowClick,
  onEditClick,
  onDeleteClick,
  page = 0,
  totalPages = 0,
  totalElements = 0,
  onPageChange
}) {
  const renderSkeletons = () => {
    return Array.from({ length: 5 }).map((_, idx) => (
      <tr key={idx}>
        <td style={{ width: '40px' }}>
          <div className="skeleton" style={{ width: '24px', height: '24px', borderRadius: '4px' }} />
        </td>
        <td>
          <div className="skeleton skeleton-title" style={{ width: '180px' }} />
        </td>
        <td>
          <div className="skeleton" style={{ width: '120px', height: '20px', borderRadius: '4px' }} />
        </td>
        <td>
          <div className="skeleton" style={{ width: '100px', height: '20px', borderRadius: '12px' }} />
        </td>
        <td style={{ width: '100px' }}>
          <div className="skeleton" style={{ width: '60px', height: '24px', borderRadius: '4px' }} />
        </td>
      </tr>
    ));
  };

  const getEntityName = (entity) => {
    if (entity.entityCategory === 'ORGANIZATION') {
      return entity.organizationDetails?.organizationName || entity.organizationName || 'N/A';
    }
    return entity.personDetails?.fullName || entity.fullName || 'N/A';
  };

  return (
    <div className="table-container animate-fade-in">
      <table className="enterprise-table">
        <thead>
          <tr>
            <th style={{ width: '40px' }}>Category</th>
            <th>Entity Name</th>
            <th>Roles / Types</th>
            <th>Status</th>
            <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            renderSkeletons()
          ) : entities.length === 0 ? (
            <tr>
              <td colSpan="5">
                <div className="empty-state">
                  <Inbox className="empty-state-icon" size={40} />
                  <h3>No Entities Found</h3>
                  <p>Create a new entity or search using a different keyword.</p>
                </div>
              </td>
            </tr>
          ) : (
            entities.map((entity) => {
              const isSelected = entity.id === selectedEntityId;
              const name = getEntityName(entity);
              
              return (
                <tr
                  key={entity.id}
                  onClick={() => onRowClick && onRowClick(entity)}
                  className={`clickable-row ${isSelected ? 'selected' : ''}`}
                >
                  <td>
                    {entity.entityCategory === 'ORGANIZATION' ? (
                      <Building2 size={18} className="logo-icon" title="Organization" />
                    ) : (
                      <User size={18} style={{ color: 'var(--color-info)' }} title="Person" />
                    )}
                  </td>
                  <td style={{ fontWeight: 600 }}>
                    {name}
                  </td>
                  <td>
                    <div className="summary-types">
                      {entity.entityTypeCodes?.map((code) => (
                        <span key={code} className="badge badge-primary">
                          {code}
                        </span>
                      )) || <span className="badge badge-neutral">No Roles</span>}
                    </div>
                  </td>
                  <td>
                    <span className={`badge ${
                      entity.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'
                    }`}>
                      {entity.status}
                    </span>
                  </td>
                  <td className="action-cell" onClick={(e) => e.stopPropagation()}>
                    <button
                      className="row-action-btn"
                      onClick={() => onEditClick && onEditClick(entity)}
                      title="Edit Entity"
                    >
                      <Edit2 size={16} />
                    </button>
                    <button
                      className="row-action-btn delete-btn"
                      onClick={() => onDeleteClick && onDeleteClick(entity)}
                      title="Soft Delete Entity"
                    >
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="pagination-container">
          <div className="pagination-info">
            Showing Page {page + 1} of {totalPages} ({totalElements} total entities)
          </div>
          <div className="pagination-controls">
            <button
              className="btn btn-secondary"
              disabled={page === 0 || loading}
              onClick={() => onPageChange(page - 1)}
            >
              <ChevronLeft size={16} />
              <span>Prev</span>
            </button>
            <button
              className="btn btn-secondary"
              disabled={page >= totalPages - 1 || loading}
              onClick={() => onPageChange(page + 1)}
            >
              <span>Next</span>
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
