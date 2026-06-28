import React, { useState, useEffect, useCallback } from 'react';
import { Search, Plus, ArrowRightLeft, History, Warehouse, Package, Calendar } from 'lucide-react';
import catalogService from '../../../services/catalogService';
import InventoryTransactionFormModal from './InventoryTransactionFormModal';
import StockTransferFormModal from './StockTransferFormModal';
import '../CatalogManagement.css';

export default function InventoryTransactionManager({ triggerNotification }) {
  // Master lists
  const [warehouses, setWarehouses] = useState([]);
  const [items, setItems] = useState([]);
  const [uoms, setUoms] = useState([]);

  // Filters
  const [selectedWarehouseId, setSelectedWarehouseId] = useState('');
  const [selectedItemId, setSelectedItemId] = useState('');

  // Transactions list state
  const [txns, setTxns] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Modals state
  const [isTxnModalOpen, setIsTxnModalOpen] = useState(false);
  const [isTransferModalOpen, setIsTransferModalOpen] = useState(false);

  // Load lookups
  const fetchLookups = useCallback(async () => {
    try {
      const whRes = await catalogService.getWarehouses(0, 1000, 'name', 'asc');
      const itemsRes = await catalogService.getItems(0, 1000, 'itemName', 'asc');
      const uomsRes = await catalogService.getUoms(0, 1000, 'code', 'asc');
      
      const whList = whRes.data?.content || [];
      const itemList = itemsRes.data?.content || [];
      const uomsList = uomsRes.data?.content || [];

      setWarehouses(whList);
      setItems(itemList);
      setUoms(uomsList);

      // Default filter selection to first warehouse if present, to auto-load initial data
      if (whList.length > 0) {
        setSelectedWarehouseId(whList[0].id);
      } else if (itemList.length > 0) {
        setSelectedItemId(itemList[0].id);
      }
    } catch (err) {
      triggerNotification('error', 'Failed to retrieve master lists for Warehouse/Items/UOMs.');
    }
  }, [triggerNotification]);

  // Load transactions based on page and filters
  const fetchTransactions = useCallback(async (pageNum = 0) => {
    // Backend validation requires at least one parameter
    if (!selectedWarehouseId && !selectedItemId) {
      setTxns([]);
      return;
    }

    setLoading(true);
    try {
      const res = await catalogService.getTransactions(
        selectedItemId || null,
        selectedWarehouseId || null,
        pageNum,
        10,
        'transactionDate',
        'desc'
      );
      const apiData = res.data || {};
      setTxns(apiData.content || []);
      setTotalPages(apiData.totalPages || 0);
      setTotalElements(apiData.totalElements || 0);
      setPage(pageNum);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to fetch transaction history.');
    } finally {
      setLoading(false);
    }
  }, [selectedWarehouseId, selectedItemId, triggerNotification]);

  useEffect(() => {
    fetchLookups();
  }, [fetchLookups]);

  useEffect(() => {
    fetchTransactions(0);
  }, [selectedWarehouseId, selectedItemId, fetchTransactions]);

  const handlePostTransaction = async (payload) => {
    try {
      await catalogService.createTransaction(payload);
      triggerNotification('success', 'Stock ledger entry posted successfully.');
      fetchTransactions(0);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to post stock transaction.');
      throw err;
    }
  };

  const handlePostTransfer = async (payload) => {
    try {
      await catalogService.transferStock(payload);
      triggerNotification('success', 'Stock transfer completed successfully.');
      fetchTransactions(0);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to transfer stock.');
      throw err;
    }
  };

  const getTxnBadgeClass = (type) => {
    switch (type) {
      case 'PURCHASE_RECEIPT':
      case 'SALES_RETURN':
        return 'badge-success';
      case 'SALES_ISSUE':
      case 'RESERVATION':
        return 'badge-primary';
      case 'PURCHASE_RETURN':
        return 'badge-danger';
      case 'TRANSFER_IN':
        return 'badge-info';
      case 'TRANSFER_OUT':
      case 'INVENTORY_ADJUSTMENT':
        return 'badge-warning';
      default:
        return 'badge-neutral';
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleString();
  };

  return (
    <div className="entity-management-page animate-fade-in">
      <div className="page-header-row">
        <div className="page-title-group">
          <h1>Stock Transaction History</h1>
          <p>Chronological audit log of all stock movements, receipts, issues, reservations, and transfers.</p>
        </div>
      </div>

      {/* Query Filter Toolbar */}
      <div className="toolbar-card">
        <div style={{ display: 'flex', gap: 'var(--space-4)', flexWrap: 'wrap', flex: 1 }}>
          <div className="form-group" style={{ margin: 0, minWidth: '220px' }}>
            <label htmlFor="filterTxnWarehouse" style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-secondary)', marginBottom: '4px', display: 'block' }}>
              Warehouse Depot Location
            </label>
            <select
              id="filterTxnWarehouse"
              value={selectedWarehouseId}
              onChange={(e) => setSelectedWarehouseId(e.target.value)}
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
            <label htmlFor="filterTxnItem" style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-secondary)', marginBottom: '4px', display: 'block' }}>
              Catalog Product
            </label>
            <select
              id="filterTxnItem"
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

        <div style={{ display: 'flex', gap: 'var(--space-2)' }}>
          <button
            className="btn btn-secondary"
            onClick={() => setIsTransferModalOpen(true)}
            style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
          >
            <ArrowRightLeft size={16} />
            <span>Transfer Stock</span>
          </button>
          
          <button
            className="btn btn-primary"
            onClick={() => setIsTxnModalOpen(true)}
            style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
          >
            <Plus size={16} />
            <span>Post Ledger</span>
          </button>
        </div>
      </div>

      {/* Warning if no filters are selected */}
      {!selectedWarehouseId && !selectedItemId && (
        <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-warning)', color: 'var(--color-warning)', backgroundColor: 'var(--color-warning-bg)' }}>
          <Calendar size={18} style={{ marginRight: '8px' }} />
          <span>Please select at least one filter criterion (Warehouse Depot or Catalog Product) to load transaction history.</span>
        </div>
      )}

      {/* Transactions details table */}
      {(selectedWarehouseId || selectedItemId) && (
        <div className="table-container animate-fade-in">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Date / Time</th>
                <th>SKU</th>
                <th>Product Description</th>
                <th>Depot Location</th>
                <th>Event Type</th>
                <th style={{ textAlign: 'right' }}>Txn Qty</th>
                <th>UOM</th>
                <th>Ref Doc / Remarks</th>
                <th>Recorded By</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                Array.from({ length: 5 }).map((_, idx) => (
                  <tr key={idx}>
                    <td><div className="skeleton" style={{ width: '120px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '160px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '120px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '100px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '60px', height: '20px', marginLeft: 'auto' }} /></td>
                    <td><div className="skeleton" style={{ width: '40px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '150px', height: '20px' }} /></td>
                    <td><div className="skeleton" style={{ width: '80px', height: '20px' }} /></td>
                  </tr>
                ))
              ) : txns.length === 0 ? (
                <tr>
                  <td colSpan="9" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                    No stock transaction records found.
                  </td>
                </tr>
              ) : (
                txns.map((t) => (
                  <tr key={t.id}>
                    <td style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)' }}>
                      {formatDate(t.transactionDate)}
                    </td>
                    <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{t.itemNumber}</td>
                    <td style={{ fontWeight: 600 }}>{t.itemName}</td>
                    <td>{t.warehouseName}</td>
                    <td>
                      <span className={`badge ${getTxnBadgeClass(t.transactionType)}`} style={{ fontSize: '0.65rem' }}>
                        {t.transactionType?.replace('_', ' ')}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 600 }}>
                      {/* Show negative indicator for issues or transfers out */}
                      {['SALES_ISSUE', 'PURCHASE_RETURN', 'TRANSFER_OUT'].includes(t.transactionType) ? '-' : ''}
                      {t.quantity}
                    </td>
                    <td style={{ fontSize: '0.85rem' }}>{t.uomCode}</td>
                    <td>
                      {t.referenceType && (
                        <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-primary)' }}>
                          Doc: {t.referenceType}
                        </div>
                      )}
                      {t.remarks && <div style={{ fontSize: '0.85rem' }}>{t.remarks}</div>}
                    </td>
                    <td style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>{t.createdBy || 'SYSTEM'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Manual single transaction entry form modal */}
      <InventoryTransactionFormModal
        isOpen={isTxnModalOpen}
        onClose={() => setIsTxnModalOpen(false)}
        onSubmit={handlePostTransaction}
        items={items}
        warehouses={warehouses}
        uoms={uoms}
      />

      {/* Stock transfer form modal */}
      <StockTransferFormModal
        isOpen={isTransferModalOpen}
        onClose={() => setIsTransferModalOpen(false)}
        onSubmit={handlePostTransfer}
        items={items}
        warehouses={warehouses}
        uoms={uoms}
      />
    </div>
  );
}
