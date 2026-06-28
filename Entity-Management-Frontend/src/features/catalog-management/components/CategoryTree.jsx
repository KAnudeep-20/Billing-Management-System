import React, { useState } from 'react';
import { ChevronDown, ChevronRight, Folder, FolderTree, Edit2, Trash2 } from 'lucide-react';

export default function CategoryTree({
  categoriesTree = [],
  onEditClick,
  onDeleteClick
}) {
  const [expandedNodeIds, setExpandedNodeIds] = useState(new Set());

  const toggleExpand = (id) => {
    setExpandedNodeIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const flattenTree = (nodes, depth = 0) => {
    let result = [];
    nodes.forEach((node) => {
      const hasChildren = node.children && node.children.length > 0;
      result.push({
        ...node,
        depth,
        hasChildren
      });

      const isExpanded = expandedNodeIds.has(node.id);
      if (isExpanded && hasChildren) {
        result = result.concat(flattenTree(node.children, depth + 1));
      }
    });
    return result;
  };

  const flattenedNodes = flattenTree(categoriesTree);

  return (
    <div className="table-container animate-fade-in" style={{ padding: 'var(--space-4)' }}>
      <div className="category-tree-container">
        <div 
          className="category-node" 
          style={{ 
            backgroundColor: 'var(--color-bg-muted)', 
            borderBottom: '2px solid var(--color-border)',
            fontWeight: 600,
            color: 'var(--color-text-secondary)',
            fontSize: '0.8rem',
            textTransform: 'uppercase',
            letterSpacing: '0.05em'
          }}
        >
          <div className="category-node-left">Hierarchy / Category Name</div>
          <div style={{ display: 'flex', gap: 'var(--space-8)', marginRight: 'var(--space-4)' }}>
            <span style={{ width: '120px' }}>Code</span>
            <span style={{ width: '100px' }}>Status</span>
            <span style={{ width: '100px', textAlign: 'right' }}>Actions</span>
          </div>
        </div>

        {flattenedNodes.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--color-text-secondary)' }}>
            <FolderTree size={36} style={{ color: 'var(--color-text-muted)', marginBottom: 'var(--space-2)' }} />
            <p>No categories defined. Click "Add Category" to get started.</p>
          </div>
        ) : (
          flattenedNodes.map((node) => {
            const isExpanded = expandedNodeIds.has(node.id);
            const indentClass = `category-indent-${Math.min(node.depth, 3)}`;

            return (
              <div 
                key={node.id} 
                className={`category-node ${indentClass}`}
                style={{ 
                  marginLeft: `${node.depth * 24}px`, 
                  width: `calc(100% - ${node.depth * 24}px)` 
                }}
              >
                <div className="category-node-left">
                  {node.hasChildren ? (
                    <button 
                      type="button" 
                      className="category-toggle-btn"
                      onClick={() => toggleExpand(node.id)}
                    >
                      {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                    </button>
                  ) : (
                    <div style={{ width: '24px' }} /> // Spacer to match toggle button width
                  )}
                  
                  <Folder size={18} style={{ color: 'var(--color-primary)', marginRight: '4px' }} />
                  <span style={{ fontWeight: 600 }}>{node.name}</span>
                  {node.description && (
                    <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginLeft: '8px' }}>
                      — {node.description}
                    </span>
                  )}
                </div>

                <div style={{ display: 'flex', gap: 'var(--space-8)', alignItems: 'center' }}>
                  <span style={{ width: '120px', fontFamily: 'monospace', fontSize: '0.85rem' }}>
                    {node.code}
                  </span>
                  
                  <span style={{ width: '100px' }}>
                    <span className={`badge ${node.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'}`}>
                      {node.status}
                    </span>
                  </span>

                  <div className="action-cell" style={{ width: '100px' }}>
                    <button
                      className="row-action-btn"
                      onClick={() => onEditClick(node)}
                      title="Edit Category"
                    >
                      <Edit2 size={15} />
                    </button>
                    <button
                      className="row-action-btn delete-btn"
                      onClick={() => onDeleteClick(node)}
                      title="Delete Category"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}
