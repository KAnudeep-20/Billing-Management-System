package com.aibilling.search.dto;

import com.aibilling.entity.model.EntityCategory;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EntitySearchResponse {
    private UUID id;
    private EntityCategory entityCategory;
    private String organizationName;
    private String fullName;
}
