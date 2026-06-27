package com.aibilling.search.controller.v1;

import com.aibilling.common.constants.AppConstants;
import com.aibilling.search.dto.EntitySearchResponse;
import com.aibilling.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_V1 + "/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/entities")
    public ResponseEntity<Page<EntitySearchResponse>> searchEntities(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<EntitySearchResponse> response = searchService.searchGlobalEntities(query, pageable);
        return ResponseEntity.ok(response);
    }
}
