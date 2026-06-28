import React, { useState, useEffect, useCallback } from 'react';
import { Search, Plus, Trash2, Edit2, Warehouse } from 'lucide-react';
import catalogService from '../../../services/catalogService';
import WarehouseFormModal from './WarehouseFormModal';
import '../CatalogManagement.css';

export default function WarehouseManager({ triggerNotification }) {
  const [warehouses, setWarehouses] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingWarehouse, setEditingWarehouse] = useState(null);

  const fetchWarehouses = useCallback(async () => {
    setLoading(true);
    try {
      const res = await catalogService.getWarehouses(0, 1000, 'name', 'asc');
      setWarehouses(res.data?.content || []);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to retrieve warehouses.');
    } finally {
      setLoading(false);
    }
  }, [triggerNotification]);

  useEffect(() => {
    fetchWarehouses();
  }, [fetchWarehouses]);

  const handleCreateOrUpdate = async (payload, editId) => {
    try {
      if (editId) {
        await catalogService.updateWarehouse(editId, payload);
        triggerNotification('success', 'Warehouse depot updated successfully.');
      } else {
        await catalogService.createWarehouse(payload);
        triggerNotification('success', 'Warehouse depot added successfully.');
      }
      fetchWarehouses();
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to save warehouse.');
      throw err;
    }
  };

  const handleDelete = async (warehouse) => {
    if (window.confirm(`Are you sure you want to delete warehouse "${warehouse.name}"?`)) {
      try {
        await catalogService.deleteWarehouse(warehouse.id);
        triggerNotification('success', 'Warehouse depot deleted successfully.');
        fetchWarehouses();
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete warehouse.');
      }
    }
  };

  const filteredWarehouses = warehouses.filter((wh) =>
    wh.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    wh.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (wh.address && wh.address.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div className="entity-management-page animate-fade-in">
      <div className="page-header-row">
        <div className="page-title-group">
          <h1>Warehouse Depots</h1>
          <p>Configure storage locations, logistics centers, and inventory staging zones.</p>
        </div>
      </div>

      {/* Toolbar */}
      <div className="toolbar-card">
        <div className="search-input-wrapper">
          <Search className="search-icon-inside" size={18} />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search warehouses by name or code..."
            aria-label="Search warehouses"
          />
        </div>

        <button
          className="btn btn-primary"
          onClick={() => {
            setEditingWarehouse(null);
            setIsModalOpen(true);
          }}
          aria-label="Add Warehouse"
        >
          <Plus size={18} />
          <span>Add Warehouse</span>
        </button>
      </div>

      {/* Table view */}
      <div className="table-container animate-fade-in">
        <table className="enterprise-table">
          <thead>
            <tr>
              <th style={{ width: '40px' }}><Warehouse size={16} /></th>
              <th>Code</th>
              <th>Warehouse Name</th>
              <th>Address / Location</th>
              <th>Status</th>
              <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              Array.from({ length: 3 }).map((_, idx) => (
                <tr key={idx}>
                  <td><div className="skeleton" style={{ width: '20px', height: '20px' }} /></td>
                  <td><div className="skeleton" style={{ width: '80px', height: '20px' }} /></td>
                  <td><div className="skeleton" style={{ width: '180px', height: '20px' }} /></td>
                  <td><div className="skeleton" style={{ width: '220px', height: '20px' }} /></td>
                  <td><div className="skeleton" style={{ width: '60px', height: '20px' }} /></td>
                  <td><div className="skeleton" style={{ width: '60px', height: '20px', marginLeft: 'auto' }} /></td>
                </tr>
              ))
            ) : filteredWarehouses.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  No warehouse depots found.
                </td>
              </tr>
            ) : (
              filteredWarehouses.map((wh) => (
                <tr key={wh.id}>
                  <td>
                    <Warehouse size={16} style={{ color: 'var(--color-text-secondary)' }} />
                  </td>
                  <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{wh.code}</td>
                  <td style={{ fontWeight: 600 }}>{wh.name}</td>
                  <td>{wh.address || <em style={{ color: 'var(--color-text-muted)' }}>No address</em>}</td>
                  <td>
                    <span className={`badge ${wh.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'}`}>
                      {wh.status}
                    </span>
                  </td>
                  <td className="action-cell">
                    <button
                      className="row-action-btn"
                      onClick={() => {
                        setEditingWarehouse(wh);
                        setIsModalOpen(true);
                      }}
                      title="Edit Warehouse"
                    >
                      <Edit2 size={16} />
                    </button>
                    <button
                      className="row-action-btn delete-btn"
                      onClick={() => handleDelete(wh)}
                      title="Delete Warehouse"
                    >
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Warehouse Modal form */}
      <WarehouseFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateOrUpdate}
        editWarehouse={editingWarehouse}
      />
    </div>
  );
}
