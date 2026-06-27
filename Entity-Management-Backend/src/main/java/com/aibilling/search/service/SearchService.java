package com.aibilling.search.service;

import com.aibilling.search.dto.EntitySearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<EntitySearchResponse> searchGlobalEntities(String query, Pageable pageable);
}
