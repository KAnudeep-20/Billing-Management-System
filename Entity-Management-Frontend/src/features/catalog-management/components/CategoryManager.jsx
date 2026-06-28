import React, { useState, useEffect, useCallback } from 'react';
import { Search, FolderPlus, HelpCircle } from 'lucide-react';
import catalogService from '../../../services/catalogService';
import CategoryTree from './CategoryTree';
import CategoryFormModal from './CategoryFormModal';
import '../CatalogManagement.css';

export default function CategoryManager({ triggerNotification }) {
  const [categoriesTree, setCategoriesTree] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);

  const fetchCategories = useCallback(async () => {
    setLoading(true);
    try {
      // 1. Fetch complete tree
      const treeRes = await catalogService.getCategoryTree();
      setCategoriesTree(treeRes.data || []);

      // 2. Fetch flat page of active categories for lookup dropdowns
      const flatRes = await catalogService.getCategories(0, 1000, 'name', 'asc');
      setAllCategories(flatRes.data?.content || []);
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to retrieve categories.');
    } finally {
      setLoading(false);
    }
  }, [triggerNotification]);

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  const handleCreateOrUpdate = async (payload, editId) => {
    try {
      if (editId) {
        await catalogService.updateCategory(editId, payload);
        triggerNotification('success', 'Category updated successfully.');
      } else {
        await catalogService.createCategory(payload);
        triggerNotification('success', 'Category created successfully.');
      }
      fetchCategories();
    } catch (err) {
      triggerNotification('error', err.message || 'Failed to save category.');
      throw err; // rethrow to keep modal open and display error inside
    }
  };

  const handleDelete = async (category) => {
    const confirmMsg = `Are you sure you want to delete category "${category.name}"?`;
    if (window.confirm(confirmMsg)) {
      try {
        await catalogService.deleteCategory(category.id);
        triggerNotification('success', 'Category deleted successfully.');
        fetchCategories();
      } catch (err) {
        triggerNotification('error', err.message || 'Failed to delete category.');
      }
    }
  };

  // Filter categories flat list for searching
  const filteredFlatCategories = allCategories.filter((cat) =>
    cat.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    cat.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
    (cat.description && cat.description.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div className="entity-management-page animate-fade-in">
      {/* Header Title */}
      <div className="page-header-row">
        <div className="page-title-group">
          <h1>Product Catalog Categories</h1>
          <p>Organize products and services in a recursive parent-child folder hierarchy.</p>
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
            placeholder="Search categories by name or code..."
            aria-label="Search categories"
          />
        </div>

        <button
          className="btn btn-primary"
          onClick={() => {
            setEditingCategory(null);
            setIsModalOpen(true);
          }}
          aria-label="Add Category"
        >
          <FolderPlus size={18} />
          <span>Add Category</span>
        </button>
      </div>

      {/* Info alert about Leaf Categories rule */}
      <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-info)', color: 'var(--color-info)', backgroundColor: 'var(--color-info-bg)', display: 'flex', gap: '8px', alignItems: 'center' }}>
        <HelpCircle size={18} />
        <span>
          <strong>Hierarchy Rule:</strong> Catalog items can only be assigned to <em>leaf categories</em> (categories with no sub-categories). Parent categories cannot directly contain items.
        </span>
      </div>

      {/* Main View: Tree View or Search Results Table */}
      {loading && categoriesTree.length === 0 ? (
        <div className="table-container" style={{ padding: 'var(--space-8)', textAlign: 'center' }}>
          <div className="spinner spinner-lg" style={{ margin: '0 auto' }} />
          <p style={{ marginTop: 'var(--space-4)', color: 'var(--color-text-secondary)' }}>Loading category hierarchy...</p>
        </div>
      ) : searchQuery.trim() !== '' ? (
        /* Flat list search results */
        <div className="table-container animate-fade-in">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Category Name</th>
                <th>Parent Category</th>
                <th>Description</th>
                <th>Status</th>
                <th style={{ width: '100px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredFlatCategories.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                    No matching categories found for query "{searchQuery}".
                  </td>
                </tr>
              ) : (
                filteredFlatCategories.map((cat) => (
                  <tr key={cat.id}>
                    <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{cat.code}</td>
                    <td style={{ fontWeight: 600 }}>{cat.name}</td>
                    <td>{cat.parentCategoryName || <em style={{ color: 'var(--color-text-muted)' }}>None (Root)</em>}</td>
                    <td>{cat.description || <em style={{ color: 'var(--color-text-muted)' }}>No description</em>}</td>
                    <td>
                      <span className={`badge ${cat.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'}`}>
                        {cat.status}
                      </span>
                    </td>
                    <td className="action-cell">
                      <button
                        className="row-action-btn"
                        onClick={() => {
                          setEditingCategory(cat);
                          setIsModalOpen(true);
                        }}
                        title="Edit Category"
                      >
                        <Edit2 size={16} />
                      </button>
                      <button
                        className="row-action-btn delete-btn"
                        onClick={() => handleDelete(cat)}
                        title="Delete Category"
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
      ) : (
        /* Standard recursive Tree View */
        <CategoryTree
          categoriesTree={categoriesTree}
          onEditClick={(cat) => {
            setEditingCategory(cat);
            setIsModalOpen(true);
          }}
          onDeleteClick={handleDelete}
        />
      )}

      {/* Category Modal Form */}
      <CategoryFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateOrUpdate}
        editCategory={editingCategory}
        allCategories={allCategories}
      />
    </div>
  );
}

// Inline imports support for nested icons if we don't import from lucide-react directly
import { Edit2, Trash2 } from 'lucide-react';
