import React from 'react';
import { Search, UserPlus } from 'lucide-react';

export default function SearchToolbar({
  query,
  onSearchChange,
  onAddClick
}) {
  return (
    <div className="toolbar-card">
      <div className="search-input-wrapper">
        <Search className="search-icon-inside" size={18} />
        <input
          type="text"
          value={query}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Search by customer, account, or contact name..."
          aria-label="Search entities"
        />
      </div>

      <button
        className="btn btn-primary"
        onClick={onAddClick}
        aria-label="Add New Entity"
      >
        <UserPlus size={18} />
        <span>Add Entity</span>
      </button>
    </div>
  );
}
