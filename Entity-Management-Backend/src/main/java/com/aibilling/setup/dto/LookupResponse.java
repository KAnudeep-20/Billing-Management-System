package com.aibilling.setup.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Unified lightweight DTO representing a simple setup lookup option.
 * Primarily used to populate dropdowns / select lists on the frontend.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupResponse {

    private UUID id;
    private String code;
    private String name;

}
