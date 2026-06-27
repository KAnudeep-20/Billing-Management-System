package com.aibilling.search.service.impl;

import com.aibilling.entity.model.Entity;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.search.dto.EntitySearchResponse;
import com.aibilling.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final EntityRepository entityRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EntitySearchResponse> searchGlobalEntities(String query, Pageable pageable) {
        Page<Entity> entities = entityRepository.searchGlobalEntities(query, pageable);
        
        return entities.map(entity -> {
            String orgName = entity.getDetails() != null ? entity.getDetails().getOrganizationName() : null;
            String fullName = entity.getDetails() != null ? entity.getDetails().getFullName() : null;
            
            return EntitySearchResponse.builder()
                    .id(entity.getId())
                    .entityCategory(entity.getEntityCategory())
                    .organizationName(orgName)
                    .fullName(fullName)
                    .build();
        });
    }
}
