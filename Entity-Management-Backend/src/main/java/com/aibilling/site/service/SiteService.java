package com.aibilling.site.service;

import com.aibilling.site.model.Site;

import java.util.List;
import java.util.UUID;

public interface SiteService {

    Site createSite(UUID accountId, Site site, List<UUID> siteUseIds);

    Site updateSite(UUID id, Site site, List<UUID> siteUseIds);

    void deleteSite(UUID id);

    Site getSiteById(UUID id);

    List<Site> getSitesByAccountId(UUID accountId);
}
