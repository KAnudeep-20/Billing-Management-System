package com.aibilling.setup.dto;

import com.aibilling.common.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO returning detailed information about a Payment Term.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTermResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer daysDue;
    private Integer displayOrder;
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
