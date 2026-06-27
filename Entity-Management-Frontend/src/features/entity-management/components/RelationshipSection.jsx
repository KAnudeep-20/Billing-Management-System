import React, { useState } from 'react';
import { GitCommit, Plus, Trash2, HelpCircle } from 'lucide-react';
import LookupSelect from './LookupSelect';

export default function RelationshipSection({
  entityId,
  relationships = [],
  relationshipTypes = [],
  allEntities = [],
  onAddRelationship,
  onDeleteRelationship
}) {
  const [relTypeId, setRelTypeId] = useState('');
  const [targetEntityId, setTargetEntityId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Filter out the current entity from other selection options
  const otherEntities = allEntities
    .filter((e) => e.id !== entityId)
    .map((e) => ({
      id: e.id,
      code: e.entityCategory,
      name: e.entityCategory === 'ORGANIZATION'
        ? e.organizationDetails?.organizationName
        : e.personDetails?.fullName
    }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!relTypeId || !targetEntityId) {
      setError('Please select both a relationship type and target entity.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onAddRelationship({
        subjectEntityId: entityId,
        relationshipTypeId: relTypeId,
        objectEntityId: targetEntityId
      });
      // Reset form
      setRelTypeId('');
      setTargetEntityId('');
    } catch (err) {
      setError(err.message || 'Failed to create relationship.');
    } finally {
      setLoading(false);
    }
  };

  const getRelationshipText = (rel) => {
    const isSubject = rel.subjectEntityId === entityId;
    const typeName = rel.relationshipType?.name || 'Related To';
    const targetName = isSubject ? rel.objectEntityName : rel.subjectEntityName;
    
    if (isSubject) {
      return (
        <span>
          This entity is <strong>{typeName}</strong> {targetName}
        </span>
      );
    } else {
      // Invert wording slightly for readability if this entity is the object
      return (
        <span>
          {targetName} is <strong>{typeName}</strong> this entity
        </span>
      );
    }
  };

  return (
    <div className="section-card animate-fade-in">
      <div className="section-header">
        <h3>
          <GitCommit size={18} style={{ color: 'var(--color-primary)' }} />
          <span>Entity Relationships</span>
        </h3>
      </div>

      <div style={{ padding: 'var(--space-6)' }}>
        {/* Existing Relationships List */}
        <div className="relationship-list" style={{ marginBottom: 'var(--space-6)' }}>
          {relationships.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 'var(--space-4)', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
              No active relationships defined for this entity.
            </div>
          ) : (
            relationships.map((rel) => (
              <div key={rel.id} className="relationship-item">
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem' }}>
                  <HelpCircle size={16} style={{ color: 'var(--color-primary)' }} />
                  {getRelationshipText(rel)}
                </div>
                <button
                  className="row-action-btn delete-btn"
                  onClick={() => onDeleteRelationship(rel.id)}
                  title="Remove Relationship"
                >
                  <Trash2 size={14} />
                </button>
              </div>
            ))
          )}
        </div>

        {/* Add New Relationship Form */}
        <form onSubmit={handleSubmit} style={{ borderTop: '1px solid var(--color-border)', paddingTop: 'var(--space-4)' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: 'var(--space-3)' }}>Define New Relationship</h4>
          
          {error && <div className="error-text" style={{ marginBottom: 'var(--space-3)' }}>{error}</div>}

          <div style={{ display: 'flex', gap: 'var(--space-4)', flexWrap: 'wrap', alignItems: 'flex-end' }}>
            <div style={{ flex: '1 1 200px' }}>
              <LookupSelect
                id="rel-type-select"
                label="Relationship Type"
                value={relTypeId}
                onChange={(e) => setRelTypeId(e.target.value)}
                options={relationshipTypes}
                placeholder="Choose relationship type..."
                required
              />
            </div>
            
            <div style={{ flex: '1 1 200px' }}>
              <LookupSelect
                id="target-entity-select"
                label="Target Entity"
                value={targetEntityId}
                onChange={(e) => setTargetEntityId(e.target.value)}
                options={otherEntities}
                placeholder="Choose target entity..."
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading || !relTypeId || !targetEntityId}
              className="btn btn-primary"
              style={{ height: '38px', padding: '0 var(--space-4)', display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <Plus size={16} />
              <span>Link</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
