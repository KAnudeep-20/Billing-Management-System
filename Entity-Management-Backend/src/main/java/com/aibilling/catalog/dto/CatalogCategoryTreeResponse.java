package com.aibilling.catalog.dto;

import com.aibilling.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Catalog Category hierarchy tree.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogCategoryTreeResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    @Builder.Default
    private List<CatalogCategoryTreeResponse> children = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<CatalogCategoryTreeResponse> getChildren() { return children; }
    public void setChildren(List<CatalogCategoryTreeResponse> children) { this.children = children; }
}
