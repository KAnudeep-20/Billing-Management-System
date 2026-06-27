package com.aibilling.common.service.impl;

import com.aibilling.audit.BaseEntity;
import com.aibilling.common.enums.Status;
import com.aibilling.common.repository.BaseRepository;
import com.aibilling.common.service.BaseService;
import com.aibilling.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Abstract base implementation of {@link BaseService}.
 *
 * <p>Provides default CRUD and soft-delete logic backed by {@link BaseRepository}.
 * Domain service implementations should extend this class:
 * <pre>
 * &#064;Service
 * public class EntityServiceImpl extends BaseServiceImpl&lt;MyEntity&gt;
 *         implements EntityService {
 *
 *     public EntityServiceImpl(EntityRepository repository) {
 *         super(repository, "Entity");
 *     }
 *
 *     // override or add domain-specific methods
 * }
 * </pre>
 *
 * @param <T> the entity type (must extend {@link BaseEntity})
 */
@Transactional(readOnly = true)
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseServiceImpl.class);

    private final BaseRepository<T> repository;
    private final String entityName;

    public BaseServiceImpl(BaseRepository<T> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    @Override
    public T findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName, "id", id.toString()));
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<T> findAllActive() {
        return repository.findByStatus(Status.ACTIVE);
    }

    @Override
    public Page<T> findAllActive(Pageable pageable) {
        return repository.findByStatus(Status.ACTIVE, pageable);
    }

    @Override
    @Transactional
    public T create(T entity) {
        entity.setStatus(Status.ACTIVE);
        T saved = repository.save(entity);
        log.info("{} created with id={}", entityName, saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public T update(UUID id, T entity) {
        T existing = findById(id);
        // Preserve the original ID and audit creation fields
        entity.setId(existing.getId());
        entity.setCreatedAt(existing.getCreatedAt());
        entity.setCreatedBy(existing.getCreatedBy());
        T updated = repository.save(entity);
        log.info("{} updated with id={}", entityName, updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        T entity = findById(id);
        repository.delete(entity);
        log.info("{} hard-deleted with id={}", entityName, id);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        T entity = findById(id);
        entity.setStatus(Status.DELETED);
        repository.save(entity);
        log.info("{} soft-deleted with id={}", entityName, id);
    }

}
