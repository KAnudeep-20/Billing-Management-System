package com.aibilling.catalog.mapper;

import com.aibilling.catalog.dto.WarehouseRequest;
import com.aibilling.catalog.dto.WarehouseResponse;
import com.aibilling.catalog.model.Warehouse;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for Warehouse domain classes.
 */
@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequest request) {
        if (request == null) return null;
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        return warehouse;
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        if (warehouse == null) return null;
        WarehouseResponse response = new WarehouseResponse();
        response.setId(warehouse.getId());
        response.setCode(warehouse.getCode());
        response.setName(warehouse.getName());
        response.setAddress(warehouse.getAddress());
        response.setStatus(warehouse.getStatus());
        response.setCreatedAt(warehouse.getCreatedAt());
        response.setCreatedBy(warehouse.getCreatedBy());
        response.setUpdatedAt(warehouse.getUpdatedAt());
        response.setUpdatedBy(warehouse.getUpdatedBy());
        return response;
    }

    public void updateEntityFromRequest(WarehouseRequest request, Warehouse warehouse) {
        if (request == null) return;
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
    }
}
