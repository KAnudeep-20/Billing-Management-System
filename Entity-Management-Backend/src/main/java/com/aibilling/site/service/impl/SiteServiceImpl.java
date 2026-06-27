package com.aibilling.site.service.impl;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.setup.model.SiteUse;
import com.aibilling.setup.repository.SiteUseRepository;
import com.aibilling.site.model.Site;
import com.aibilling.site.model.SiteUseMapping;
import com.aibilling.site.repository.SiteRepository;
import com.aibilling.site.repository.SiteUseMappingRepository;
import com.aibilling.site.service.SiteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final SiteUseMappingRepository siteUseMappingRepository;
    private final AccountRepository accountRepository;
    private final SiteUseRepository siteUseRepository;

    public SiteServiceImpl(SiteRepository siteRepository,
                           SiteUseMappingRepository siteUseMappingRepository,
                           AccountRepository accountRepository,
                           SiteUseRepository siteUseRepository) {
        this.siteRepository = siteRepository;
        this.siteUseMappingRepository = siteUseMappingRepository;
        this.accountRepository = accountRepository;
        this.siteUseRepository = siteUseRepository;
    }

    @Override
    @Transactional
    public Site createSite(UUID accountId, Site site, List<UUID> siteUseIds) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId.toString()));

        if (account.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot add site to a deleted Account");
        }

        // Fetch site uses
        List<SiteUse> siteUses = fetchSiteUses(siteUseIds);
        
        // Business Rule: If this is the first site, it MUST be Primary
        long activeSitesCount = siteRepository.countByAccountIdAndStatusNot(accountId, Status.DELETED);
        if (activeSitesCount == 0 && !hasPrimaryUse(siteUses)) {
            throw new BusinessException("The first site created for an account must be a PRIMARY site.");
        }

        site.setAccount(account);
        site.setStatus(Status.ACTIVE);
        
        // Save site first to generate ID
        Site savedSite = siteRepository.save(site);

        // Save site uses
        List<SiteUseMapping> mappings = new ArrayList<>();
        for (SiteUse su : siteUses) {
            SiteUseMapping mapping = new SiteUseMapping();
            mapping.setSite(savedSite);
            mapping.setSiteUse(su);
            mapping.setStatus(Status.ACTIVE);
            mappings.add(siteUseMappingRepository.save(mapping));
        }
        
        savedSite.setSiteUses(mappings);
        return savedSite;
    }

    @Override
    @Transactional
    public Site updateSite(UUID id, Site siteUpdate, List<UUID> siteUseIds) {
        Site existingSite = getSiteById(id);
        
        if (existingSite.getStatus() == Status.DELETED) {
            throw new BusinessException("Cannot update a deleted site");
        }

        List<SiteUse> newSiteUses = fetchSiteUses(siteUseIds);
        
        // Business Rule: Ensure we aren't removing the only PRIMARY site
        if (!hasPrimaryUse(newSiteUses)) {
            boolean isCurrentlyPrimary = hasPrimaryUse(existingSite.getSiteUses().stream()
                    .filter(m -> m.getStatus() != Status.DELETED)
                    .map(SiteUseMapping::getSiteUse).toList());
            
            if (isCurrentlyPrimary) {
                // Check if other primary sites exist for this account
                if (!hasOtherPrimarySites(existingSite.getAccount().getId(), existingSite.getId())) {
                    throw new BusinessException("Cannot remove PRIMARY use from this site as it is the only primary site for the account.");
                }
            }
        }

        // Update fields
        existingSite.setSiteName(siteUpdate.getSiteName());
        existingSite.setAddressLine1(siteUpdate.getAddressLine1());
        existingSite.setAddressLine2(siteUpdate.getAddressLine2());
        existingSite.setAddressLine3(siteUpdate.getAddressLine3());
        existingSite.setCity(siteUpdate.getCity());
        existingSite.setState(siteUpdate.getState());
        existingSite.setPostalCode(siteUpdate.getPostalCode());
        existingSite.setCountry(siteUpdate.getCountry());
        
        if (siteUpdate.getStatus() != null && siteUpdate.getStatus() != existingSite.getStatus()) {
            if (siteUpdate.getStatus() == Status.DELETED) {
                throw new BusinessException("Please use the delete endpoint to delete a site.");
            }
            existingSite.setStatus(siteUpdate.getStatus());
        }

        // Replace site uses: Delete old, add new
        siteUseMappingRepository.deleteBySiteId(existingSite.getId());
        existingSite.getSiteUses().clear();
        
        for (SiteUse su : newSiteUses) {
            SiteUseMapping mapping = new SiteUseMapping();
            mapping.setSite(existingSite);
            mapping.setSiteUse(su);
            mapping.setStatus(Status.ACTIVE);
            existingSite.getSiteUses().add(siteUseMappingRepository.save(mapping));
        }

        return siteRepository.save(existingSite);
    }

    @Override
    @Transactional
    public void deleteSite(UUID id) {
        Site site = getSiteById(id);
        
        if (site.getStatus() == Status.DELETED) {
            return;
        }

        UUID accountId = site.getAccount().getId();
        
        // Business Rule 1: At least one site required
        long activeSitesCount = siteRepository.countByAccountIdAndStatusNot(accountId, Status.DELETED);
        if (activeSitesCount <= 1) {
            throw new BusinessException("Cannot delete the final site of an account. Each account must have at least one active site.");
        }

        // Business Rule 2: Save must fail if no primary site exists
        boolean isPrimary = hasPrimaryUse(site.getSiteUses().stream()
                .filter(m -> m.getStatus() != Status.DELETED)
                .map(SiteUseMapping::getSiteUse).toList());
        
        if (isPrimary && !hasOtherPrimarySites(accountId, id)) {
            throw new BusinessException("Cannot delete this site because it is the only PRIMARY site for the account.");
        }

        site.setStatus(Status.DELETED);
        siteRepository.save(site);
        
        // Soft delete mappings
        site.getSiteUses().forEach(m -> {
            m.setStatus(Status.DELETED);
            siteUseMappingRepository.save(m);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Site getSiteById(UUID id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site", "id", id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Site> getSitesByAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", "id", accountId.toString());
        }
        return siteRepository.findByAccountIdAndStatusNot(accountId, Status.DELETED);
    }

    // Helper Methods

    private List<SiteUse> fetchSiteUses(List<UUID> siteUseIds) {
        List<SiteUse> siteUses = new ArrayList<>();
        if (siteUseIds != null) {
            for (UUID useId : siteUseIds) {
                SiteUse su = siteUseRepository.findById(useId)
                        .orElseThrow(() -> new ResourceNotFoundException("SiteUse", "id", useId.toString()));
                siteUses.add(su);
            }
        }
        return siteUses;
    }

    private boolean hasPrimaryUse(List<SiteUse> siteUses) {
        return siteUses.stream().anyMatch(su -> "PRIMARY".equals(su.getCode()));
    }

    private boolean hasOtherPrimarySites(UUID accountId, UUID currentSiteId) {
        List<Site> allSites = siteRepository.findByAccountIdAndStatusNot(accountId, Status.DELETED);
        for (Site s : allSites) {
            if (!s.getId().equals(currentSiteId)) {
                boolean isPrimary = s.getSiteUses().stream()
                        .filter(m -> m.getStatus() != Status.DELETED)
                        .anyMatch(m -> "PRIMARY".equals(m.getSiteUse().getCode()));
                if (isPrimary) {
                    return true;
                }
            }
        }
        return false;
    }
}
