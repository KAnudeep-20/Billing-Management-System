package com.aibilling.entity.service.impl;

import com.aibilling.account.dto.AccountHierarchyResponse;
import com.aibilling.account.dto.AccountResponse;
import com.aibilling.account.mapper.AccountMapper;
import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.contact.dto.ContactResponse;
import com.aibilling.contact.mapper.ContactMapper;
import com.aibilling.contact.model.Contact;
import com.aibilling.contact.repository.ContactRepository;
import com.aibilling.entity.dto.CompleteEntityDetailsResponse;
import com.aibilling.entity.dto.EntityResponse;
import com.aibilling.entity.mapper.EntityMapper;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.entity.service.EntityAggregationService;
import com.aibilling.exception.ResourceNotFoundException;
import com.aibilling.relationship.dto.EntityRelationshipResponse;
import com.aibilling.relationship.mapper.EntityRelationshipMapper;
import com.aibilling.relationship.model.EntityRelationship;
import com.aibilling.relationship.repository.EntityRelationshipRepository;
import com.aibilling.site.dto.SiteResponse;
import com.aibilling.site.mapper.SiteMapper;
import com.aibilling.site.model.Site;
import com.aibilling.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntityAggregationServiceImpl implements EntityAggregationService {

    private final EntityRepository entityRepository;
    private final EntityMapper entityMapper;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    private final EntityRelationshipRepository relationshipRepository;
    private final EntityRelationshipMapper relationshipMapper;

    @Override
    @Transactional(readOnly = true)
    public CompleteEntityDetailsResponse getCompleteEntityDetails(UUID entityId) {
        // 1. Fetch Entity (1 query)
        Entity entity = entityRepository.findByIdWithDetails(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", entityId.toString()));
        EntityResponse entityResponse = entityMapper.toResponse(entity);

        // 2. Fetch Relationships (1 query)
        List<EntityRelationship> relationships = relationshipRepository.findByEntityIdAndStatusNot(entityId, Status.DELETED);
        List<EntityRelationshipResponse> relationshipResponses = relationships.stream()
                .map(relationshipMapper::toResponse)
                .collect(Collectors.toList());

        // 3. Fetch Accounts (1 query)
        List<Account> accounts = accountRepository.findByEntityIdAndStatusNotWithDetails(entityId, Status.DELETED);
        List<UUID> accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());

        // 4. Fetch Sites (1 query) and Contacts (1 query) based on Account IDs
        Map<UUID, List<SiteResponse>> sitesByAccount = Collections.emptyMap();
        Map<UUID, List<ContactResponse>> contactsByAccount = Collections.emptyMap();

        if (!accountIds.isEmpty()) {
            List<Site> sites = siteRepository.findByAccountIdInAndStatusNot(accountIds, Status.DELETED);
            sitesByAccount = sites.stream()
                    .map(siteMapper::toResponse)
                    .collect(Collectors.groupingBy(SiteResponse::getAccountId));

            List<Contact> contacts = contactRepository.findByAccountIdInAndStatusNot(accountIds, Status.DELETED);
            contactsByAccount = contacts.stream()
                    .map(contactMapper::toResponse)
                    .collect(Collectors.groupingBy(ContactResponse::getAccountId));
        }

        final Map<UUID, List<SiteResponse>> finalSitesByAccount = sitesByAccount;
        final Map<UUID, List<ContactResponse>> finalContactsByAccount = contactsByAccount;

        // 5. Assemble Hierarchy in Memory
        List<AccountHierarchyResponse> accountHierarchies = accounts.stream().map(account -> {
            AccountResponse baseResponse = accountMapper.toResponse(account);
            AccountHierarchyResponse hierarchyResponse = new AccountHierarchyResponse();
            BeanUtils.copyProperties(baseResponse, hierarchyResponse);

            hierarchyResponse.setSites(finalSitesByAccount.getOrDefault(account.getId(), Collections.emptyList()));
            hierarchyResponse.setContacts(finalContactsByAccount.getOrDefault(account.getId(), Collections.emptyList()));

            return hierarchyResponse;
        }).collect(Collectors.toList());

        // 6. Final Assembly
        CompleteEntityDetailsResponse response = new CompleteEntityDetailsResponse();
        response.setEntitySummary(entityResponse);
        response.setRelationships(relationshipResponses);
        response.setAccounts(accountHierarchies);

        return response;
    }
}
