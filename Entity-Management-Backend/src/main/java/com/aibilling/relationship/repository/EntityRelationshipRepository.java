package com.aibilling.relationship.repository;

import com.aibilling.common.enums.Status;
import com.aibilling.relationship.model.EntityRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EntityRelationshipRepository extends JpaRepository<EntityRelationship, UUID> {

    @Query("SELECT r FROM EntityRelationship r " +
           "JOIN FETCH r.subjectEntity " +
           "JOIN FETCH r.relationshipType " +
           "JOIN FETCH r.objectEntity " +
           "WHERE r.status != :status")
    List<EntityRelationship> findAllActive(@Param("status") Status status);

    @Query("SELECT r FROM EntityRelationship r " +
           "JOIN FETCH r.subjectEntity " +
           "JOIN FETCH r.relationshipType " +
           "JOIN FETCH r.objectEntity " +
           "WHERE r.subjectEntity.id = :subjectId AND r.status != :status")
    List<EntityRelationship> findBySubjectEntityIdAndStatusNot(@Param("subjectId") UUID subjectId, @Param("status") Status status);

    @Query("SELECT r FROM EntityRelationship r " +
           "JOIN FETCH r.subjectEntity " +
           "JOIN FETCH r.relationshipType " +
           "JOIN FETCH r.objectEntity " +
           "WHERE r.objectEntity.id = :objectId AND r.status != :status")
    List<EntityRelationship> findByObjectEntityIdAndStatusNot(@Param("objectId") UUID objectId, @Param("status") Status status);

    @Query("SELECT r FROM EntityRelationship r " +
           "JOIN FETCH r.subjectEntity " +
           "JOIN FETCH r.relationshipType " +
           "JOIN FETCH r.objectEntity " +
           "WHERE (r.subjectEntity.id = :entityId OR r.objectEntity.id = :entityId) AND r.status != :status")
    List<EntityRelationship> findByEntityIdAndStatusNot(@Param("entityId") UUID entityId, @Param("status") Status status);

    boolean existsBySubjectEntityIdAndRelationshipTypeIdAndObjectEntityIdAndStatusNot(
            UUID subjectEntityId, UUID relationshipTypeId, UUID objectEntityId, Status status);
}
