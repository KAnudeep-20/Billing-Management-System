import React from 'react';

export default function LookupSelect({
  id,
  label,
  value,
  onChange,
  options = [],
  placeholder = 'Select option...',
  required = false,
  error = '',
  disabled = false,
  name
}) {
  return (
    <div className="form-group">
      {label && (
        <label htmlFor={id} className={required ? 'required' : ''}>
          {label}
        </label>
      )}
      <select
        id={id}
        name={name}
        value={value || ''}
        onChange={onChange}
        required={required}
        disabled={disabled}
        className={`form-control ${error ? 'error' : ''}`}
      >
        <option value="">{placeholder}</option>
        {options.map((opt) => (
          <option key={opt.id} value={opt.id}>
            {opt.name} ({opt.code})
          </option>
        ))}
      </select>
      {error && <span className="error-text">{error}</span>}
    </div>
  );
}
