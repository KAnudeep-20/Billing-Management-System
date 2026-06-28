package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.UomRequest;
import com.aibilling.catalog.dto.UomResponse;
import com.aibilling.catalog.model.Uom;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for UOM domain classes.
 */
@Component
public class UomMapper {

    public Uom toEntity(UomRequest request) {
        if (request == null) return null;
        Uom uom = new Uom();
        uom.setCode(request.getCode());
        uom.setDescription(request.getDescription());
        return uom;
    }

    public UomResponse toResponse(Uom uom) {
        if (uom == null) return null;
        UomResponse response = new UomResponse();
        response.setId(uom.getId());
        response.setCode(uom.getCode());
        response.setDescription(uom.getDescription());
        response.setStatus(uom.getStatus());
        response.setCreatedAt(uom.getCreatedAt());
        response.setCreatedBy(uom.getCreatedBy());
        response.setUpdatedAt(uom.getUpdatedAt());
        response.setUpdatedBy(uom.getUpdatedBy());
        return response;
    }

    public void updateEntityFromRequest(UomRequest request, Uom uom) {
        if (request == null) return;
        uom.setCode(request.getCode());
        uom.setDescription(request.getDescription());
    }
}
