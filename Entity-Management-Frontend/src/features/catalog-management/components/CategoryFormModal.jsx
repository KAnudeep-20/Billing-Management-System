import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export default function CategoryFormModal({
  isOpen,
  onClose,
  onSubmit,
  editCategory,
  allCategories = []
}) {
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
    parentCategoryId: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (editCategory) {
      setFormData({
        code: editCategory.code || '',
        name: editCategory.name || '',
        description: editCategory.description || '',
        parentCategoryId: editCategory.parentCategoryId || ''
      });
    } else {
      setFormData({
        code: '',
        name: '',
        description: '',
        parentCategoryId: ''
      });
    }
    setErrors({});
  }, [editCategory, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.code.trim()) newErrors.code = 'Category code is required.';
    else if (formData.code.length > 50) newErrors.code = 'Code must not exceed 50 characters.';
    
    if (!formData.name.trim()) newErrors.name = 'Category name is required.';
    else if (formData.name.length > 100) newErrors.name = 'Name must not exceed 100 characters.';
    
    if (formData.description && formData.description.length > 255) {
      newErrors.description = 'Description must not exceed 255 characters.';
    }

    if (editCategory && formData.parentCategoryId === editCategory.id) {
      newErrors.parentCategoryId = 'A category cannot be its own parent.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      const payload = {
        code: formData.code.trim().toUpperCase(),
        name: formData.name.trim(),
        description: formData.description.trim() || null,
        parentCategoryId: formData.parentCategoryId || null
      };
      await onSubmit(payload, editCategory?.id);
      onClose();
    } catch (err) {
      console.error(err);
      setErrors({ apiError: err.message || 'Operation failed. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  // Filter valid parent categories:
  // 1. Cannot be the category itself (if editing)
  // 2. Cannot have items assigned (business rule: parent categories cannot hold items)
  const parentOptions = allCategories.filter((cat) => {
    if (editCategory && cat.id === editCategory.id) return false;
    return true; // We show them, but we will disable them if they have items.
  });

  return createPortal(
    <div className="catalog-drawer-overlay" onClick={onClose}>
      <div className="catalog-drawer-container" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-header">
          <h2>{editCategory ? 'Edit Catalog Category' : 'Add New Category'}</h2>
          <button className="drawer-close-btn" onClick={onClose} aria-label="Close drawer">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden' }}>
          <div className="drawer-body">
            {errors.apiError && (
              <div className="placeholder-warning-alert" style={{ borderColor: 'var(--color-error)', color: 'var(--color-error)', backgroundColor: 'var(--color-error-bg)', marginBottom: 'var(--space-4)', padding: 'var(--space-2)' }}>
                <span>{errors.apiError}</span>
              </div>
            )}

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="code" className="required">Category Code</label>
              <input
                type="text"
                id="code"
                name="code"
                value={formData.code}
                onChange={handleChange}
                placeholder="e.g. ELECTRONICS"
                disabled={loading}
                className={`form-control ${errors.code ? 'error' : ''}`}
              />
              {errors.code && <span className="error-text">{errors.code}</span>}
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="name" className="required">Category Name</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="e.g. Consumer Electronics"
                disabled={loading}
                className={`form-control ${errors.name ? 'error' : ''}`}
              />
              {errors.name && <span className="error-text">{errors.name}</span>}
            </div>

            <div className="form-group" style={{ marginBottom: 'var(--space-4)' }}>
              <label htmlFor="parentCategoryId">Parent Category</label>
              <select
                id="parentCategoryId"
                name="parentCategoryId"
                value={formData.parentCategoryId}
                onChange={handleChange}
                disabled={loading}
                className={`form-control ${errors.parentCategoryId ? 'error' : ''}`}
              >
                <option value="">-- None (Root Category) --</option>
                {parentOptions.map((cat) => {
                  const isDisabled = cat.hasItems;
                  return (
                    <option 
                      key={cat.id} 
                      value={cat.id} 
                      disabled={isDisabled}
                    >
                      {cat.name} ({cat.code}) {isDisabled ? '[Has Items - Cannot be Parent]' : ''}
                    </option>
                  );
                })}
              </select>
              {errors.parentCategoryId && <span className="error-text">{errors.parentCategoryId}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Provide a brief description..."
                rows={3}
                disabled={loading}
                className={`form-control ${errors.description ? 'error' : ''}`}
                style={{ resize: 'vertical' }}
              />
              {errors.description && <span className="error-text">{errors.description}</span>}
            </div>
          </div>

          <div className="drawer-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? <div className="spinner" /> : editCategory ? 'Save Changes' : 'Create Category'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body
  );
}
