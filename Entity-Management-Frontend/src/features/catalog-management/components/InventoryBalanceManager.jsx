import React, { useState, useEffect, useCallback } from 'react';
import { Search, BarChart2, Warehouse, Package, HelpCircle } from 'lucide-react';
import catalogService from '../../../services/catalogService';
import '../CatalogManagement.css';

export default function InventoryBalanceManager({ triggerNotification }) {
  const [warehouses, setWarehouses] = useState([]);
  const [items, setItems] = useState([]);
  const [balances, setBalances] = useState([]);

  // Selected filters
  const [selectedWarehouseId, setSelectedWarehouseId] = useState('');
  const [selectedItemId, setSelectedItemId] = useState('');
  const [loading, setLoading] = useState(false);

  // Fetch warehouse & item lookup data
  const fetchLookups = useCallback(async () => {
    try {
      const whRes = await catalogService.getWarehouses(0, 1000, 'name', 'asc');
      const itemsRes = await catalogService.getItems(0, 1000, 'itemName', 'asc');
      
      const whList = whRes.data?.content || [];
      const itemList = itemsRes.data?.content || [];

      setWarehouses(whList);
      setItems(itemList);

      // Default filter selection to first warehouse if present, to auto-load initial data
      if (whList.length > 0) {
        setSelectedWarehouseId(whList[0].id);
      } else if (itemList.length > 0) {
        setSelectedItemId(itemList[0].id);
      }
    } catch (err) {
      triggerNotification('error', 'Failed to retrieve master lists for Warehouse/Items.');
    }
  }, [triggerNotification]);

  // Fetch balances based on selected filters
  const fetchBalances = useCallback(async () => {
    // Backend validation requires at least one parameter
    if (!selectedWarehouseId && !selectedItemId) {
      setBalances([]);
      return;
    }

    setLoading(true);
    try {
      const res = await catalogService.getBalances(
        selectedItemId || null,
        selectedWarehouseId || null
      );
      setBalances(res.data || []);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to fetch inventory balances.');
    } finally {
      setLoading(false);
    }
  }, [selectedWarehouseId, selectedItemId, triggerNotification]);

  useEffect(() => {
    fetchLookups();
  }, [fetchLookups]);

  useEffect(() => {
    fetchBalances();
  }, [fetchBalances]);

  // Calculate sum totals for summary cards
  const totalOnHand = balances.reduce((sum, bal) => sum + parseFloat(bal.quantityOnHand || 0), 0);
  const totalReserved = balances.reduce((sum, bal) => sum + parseFloat(bal.reservedQty || 0), 0);
  const totalAvailable = balances.reduce((sum, bal) => sum + parseFloat(bal.availableQty || 0), 0);

  return (
    <div className="entity-management-page animate-fade-in">
      <div className="page-header-row">
        <div className="page-title-group">
          <h1>Stock Inventory Balance</h1>
          <p>Real-time ledger overview of quantities on-hand, reserved, and available for shipment.</p>
        </div>
      </div>

      {/* Query Filter Toolbar */}
      <div className="toolbar-card">
        <div style={{ display: 'flex', gap: 'var(--space-4)', flexWrap: 'wrap', width: '100%' }}>
          <div className="form-group" style={{ margin: 0, minWidth: '220px' }}>
            <label htmlFor="filterWarehouse" style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-secondary)', marginBottom: '4px', display: 'block' }}>
              Warehouse Depot Location
            </label>
            <select
              id="filterWarehouse"
              value={selectedWarehouseId}
              onChange={(e) => {
                setSelectedWarehouseId(e.target.value);
                // Clear the other filter if we want to search single-dimension, or keep both
              }}
              className="filter-select"
              style={{ width: '100%', padding: 'var(--space-2)' }}
            >
              <option value="">-- All Warehouses (Must select product) --</option>
              {warehouses.map((wh) => (
                <option key={wh.id} value={wh.id}>
                  {wh.name} ({wh.code})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group" style={{ margin: 0, minWidth: '220px' }}>
            <label htmlFor="filterItem" style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-secondary)', marginBottom: '4px', display: 'block' }}>
              Catalog Product
            </label>
            <select
              id="filterItem"
              value={selectedItemId}
              onChange={(e) => setSelectedItemId(e.target.value)}
              className="filter-select"
              style={{ width: '100%', padding: 'var(--space-2)' }}
            >
              <option value="">-- All Products (Must select warehouse) --</option>
              {items.filter(i => i.isInventoryTracked).map((item) => (
                <option key={item.id} value={item.id}>
                  {item.itemName} ({item.itemNumber})
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Warning if no filters are selected */}
      {!selectedWarehouseId && !selectedItemId && (
        <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-warning)', color: 'var(--color-warning)', backgroundColor: 'var(--color-warning-bg)' }}>
          <HelpCircle size={18} style={{ marginRight: '8px' }} />
          <span>Please select at least one filter criterion (Warehouse Depot or Catalog Product) to load inventory balances.</span>
        </div>
      )}

      {/* Aggregate metrics cards */}
      {(selectedWarehouseId || selectedItemId) && (
        <div className="balance-cards-grid animate-fade-in">
          <div className="balance-card" style={{ borderLeft: '4px solid var(--color-primary)' }}>
            <div className="balance-card-lbl">Total On Hand</div>
            <div className="balance-card-val">{totalOnHand}</div>
          </div>
          <div className="balance-card" style={{ borderLeft: '4px solid var(--color-warning)' }}>
            <div className="balance-card-lbl">Total Reserved</div>
            <div className="balance-card-val">{totalReserved}</div>
          </div>
          <div className="balance-card" style={{ borderLeft: '4px solid var(--color-success)' }}>
            <div className="balance-card-lbl">Total Available</div>
            <div className="balance-card-val" style={{ color: 'var(--color-success)' }}>{totalAvailable}</div>
          </div>
        </div>
      )}

      {/* Balances details table */}
      {(selectedWarehouseId || selectedItemId) && (
        <div className="table-container animate-fade-in">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>SKU / SKU Number</th>
                <th>Item Description</th>
                <th>Warehouse Depot</th>
                <th style={{ textAlign: 'right' }}>Qty On Hand</th>
                <th style={{ textAlign: 'right' }}>Qty Reserved</th>
                <th style={{ textAlign: 'right' }}>Qty Available</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 4 }).map((_, idx) => (
                  <tr key={idx}>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '180px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '120px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px', marginLeft: 'auto' }} /></td>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px', marginLeft: 'auto' }} /></td>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px', marginLeft: 'auto' }} /></td>
                  </tr>
                ))
              ) : balances.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                    No stock records exist for the selected filters.
                  </td>
                </tr>
              ) : (
                balances.map((bal) => (
                  <tr key={bal.id}>
                    <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{bal.itemNumber}</td>
                    <td style={{ fontWeight: 600 }}>{bal.itemName}</td>
                    <td>
                      <Warehouse size={14} style={{ marginRight: '6px', color: 'var(--color-text-secondary)', verticalAlign: 'middle' }} />
                      <span>{bal.warehouseName}</span>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 600 }}>{bal.quantityOnHand}</td>
                    <td style={{ textAlign: 'right', color: 'var(--color-warning)' }}>{bal.reservedQty}</td>
                    <td style={{ textAlign: 'right', color: 'var(--color-success)', fontWeight: 600 }}>{bal.availableQty}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
