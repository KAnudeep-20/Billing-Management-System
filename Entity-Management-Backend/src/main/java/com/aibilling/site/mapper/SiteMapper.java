package com.aibilling.site.mapper;

import com.aibilling.common.enums.Status;
import com.aibilling.setup.dto.LookupResponse;
import com.aibilling.site.dto.SiteCreateRequest;
import com.aibilling.site.dto.SiteResponse;
import com.aibilling.site.dto.SiteUpdateRequest;
import com.aibilling.site.model.Site;
import com.aibilling.site.model.SiteUseMapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SiteMapper {

    public Site toEntity(SiteCreateRequest request) {
        if (request == null) {
            return null;
        }

        Site site = new Site();
        site.setSiteName(request.getSiteName());
        site.setAddressLine1(request.getAddressLine1());
        site.setAddressLine2(request.getAddressLine2());
        site.setAddressLine3(request.getAddressLine3());
        site.setCity(request.getCity());
        site.setState(request.getState());
        site.setPostalCode(request.getPostalCode());
        site.setCountry(request.getCountry());
        site.setStatus(Status.ACTIVE);

        return site;
    }

    public Site toEntity(SiteUpdateRequest request) {
        if (request == null) {
            return null;
        }

        Site site = new Site();
        site.setSiteName(request.getSiteName());
        site.setAddressLine1(request.getAddressLine1());
        site.setAddressLine2(request.getAddressLine2());
        site.setAddressLine3(request.getAddressLine3());
        site.setCity(request.getCity());
        site.setState(request.getState());
        site.setPostalCode(request.getPostalCode());
        site.setCountry(request.getCountry());
        site.setStatus(request.getStatus());

        return site;
    }

    public SiteResponse toResponse(Site site) {
        if (site == null) {
            return null;
        }

        SiteResponse response = new SiteResponse();
        response.setId(site.getId());
        if (site.getAccount() != null) {
            response.setAccountId(site.getAccount().getId());
        }
        response.setSiteName(site.getSiteName());
        response.setAddressLine1(site.getAddressLine1());
        response.setAddressLine2(site.getAddressLine2());
        response.setAddressLine3(site.getAddressLine3());
        response.setCity(site.getCity());
        response.setState(site.getState());
        response.setPostalCode(site.getPostalCode());
        response.setCountry(site.getCountry());
        response.setStatus(site.getStatus());
        response.setCreatedAt(site.getCreatedAt());
        response.setUpdatedAt(site.getUpdatedAt());

        // Concatenated address
        response.setConcatenatedAddress(buildConcatenatedAddress(site));

        // Map Site Uses
        if (site.getSiteUses() != null) {
            List<LookupResponse> siteUseResponses = site.getSiteUses().stream()
                    .filter(mapping -> mapping.getStatus() != Status.DELETED)
                    .map(mapping -> {
                        LookupResponse lookup = new LookupResponse();
                        lookup.setId(mapping.getSiteUse().getId());
                        lookup.setCode(mapping.getSiteUse().getCode());
                        lookup.setName(mapping.getSiteUse().getName());
                        return lookup;
                    })
                    .collect(Collectors.toList());
            response.setSiteUses(siteUseResponses);
        }

        return response;
    }

    private String buildConcatenatedAddress(Site site) {
        List<String> parts = new ArrayList<>();
        if (site.getAddressLine1() != null && !site.getAddressLine1().isBlank()) parts.add(site.getAddressLine1());
        if (site.getAddressLine2() != null && !site.getAddressLine2().isBlank()) parts.add(site.getAddressLine2());
        if (site.getAddressLine3() != null && !site.getAddressLine3().isBlank()) parts.add(site.getAddressLine3());
        
        String cityStateZip = "";
        if (site.getCity() != null && !site.getCity().isBlank()) cityStateZip += site.getCity();
        if (site.getState() != null && !site.getState().isBlank()) {
            if (!cityStateZip.isEmpty()) cityStateZip += ", ";
            cityStateZip += site.getState();
        }
        if (site.getPostalCode() != null && !site.getPostalCode().isBlank()) {
            if (!cityStateZip.isEmpty()) cityStateZip += " ";
            cityStateZip += site.getPostalCode();
        }
        if (!cityStateZip.isEmpty()) parts.add(cityStateZip);

        if (site.getCountry() != null && !site.getCountry().isBlank()) parts.add(site.getCountry());

        return String.join("\n", parts);
    }
}
