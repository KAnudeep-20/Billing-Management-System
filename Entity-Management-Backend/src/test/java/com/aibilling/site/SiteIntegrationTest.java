package com.aibilling.site;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.setup.model.SiteUse;
import com.aibilling.setup.repository.SiteUseRepository;
import com.aibilling.site.dto.SiteCreateRequest;
import com.aibilling.site.dto.SiteUpdateRequest;
import com.aibilling.site.repository.SiteRepository;
import com.aibilling.site.repository.SiteUseMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SiteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteUseMappingRepository siteUseMappingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private SiteUseRepository siteUseRepository;

    @Autowired
    private com.aibilling.contact.repository.ContactRepository contactRepository;

    @Autowired
    private com.aibilling.relationship.repository.EntityRelationshipRepository entityRelationshipRepository;

    private UUID accountId;
    private UUID primarySiteUseId;
    private UUID billToSiteUseId;
    private UUID shipToSiteUseId;

    @BeforeEach
    void setUp() {








        // Seed Site Uses
        SiteUse primary = new SiteUse();
        primary.setCode("PRIMARY");
        primary.setName("Primary");
        primary = siteUseRepository.save(primary);
        primarySiteUseId = primary.getId();

        SiteUse billTo = new SiteUse();
        billTo.setCode("BILL_TO");
        billTo.setName("Bill To");
        billTo = siteUseRepository.save(billTo);
        billToSiteUseId = billTo.getId();

        SiteUse shipTo = new SiteUse();
        shipTo.setCode("SHIP_TO");
        shipTo.setName("Ship To");
        shipTo = siteUseRepository.save(shipTo);
        shipToSiteUseId = shipTo.getId();

        // Seed Entity
        Entity entity = new Entity();
        entity.setEntityCategory(EntityCategory.ORGANIZATION);
        entity = entityRepository.save(entity);

        // Seed Account
        Account account = new Account();
        account.setEntity(entity);
        account.setAccountName("Test Account");
        account = accountRepository.save(account);
        accountId = account.getId();
    }

    @Test
    void createSite_withPrimaryUse_succeeds() throws Exception {
        SiteCreateRequest request = new SiteCreateRequest();
        request.setAccountId(accountId);
        request.setSiteName("Main HQ");
        request.setAddressLine1("123 Main St");
        request.setCity("San Francisco");
        request.setState("CA");
        request.setPostalCode("94105");
        request.setCountry("USA");
        request.setSiteUseIds(List.of(primarySiteUseId, billToSiteUseId));

        mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.siteName").value("Main HQ"))
                .andExpect(jsonPath("$.data.concatenatedAddress").value("123 Main St\nSan Francisco, CA 94105\nUSA"))
                .andExpect(jsonPath("$.data.siteUses.length()").value(2));
    }

    @Test
    void createSite_whenFirstSite_withoutPrimaryUse_failsWithBusinessException() throws Exception {
        SiteCreateRequest request = new SiteCreateRequest();
        request.setAccountId(accountId);
        request.setSiteName("Branch Office");
        request.setAddressLine1("456 Branch St");
        request.setSiteUseIds(List.of(billToSiteUseId)); // No primary

        mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("The first site created for an account must be a PRIMARY site."));
    }

    @Test
    void updateSite_succeeds() throws Exception {
        // Create primary site first
        SiteCreateRequest createReq = new SiteCreateRequest();
        createReq.setAccountId(accountId);
        createReq.setSiteName("Initial Site");
        createReq.setAddressLine1("123 St");
        createReq.setSiteUseIds(List.of(primarySiteUseId));
        
        MvcResult result = mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String siteIdStr = objectMapper.readTree(responseBody).get("data").get("id").asText();

        // Update site
        SiteUpdateRequest updateReq = new SiteUpdateRequest();
        updateReq.setSiteName("Updated Site");
        updateReq.setAddressLine1("456 Updated St");
        updateReq.setSiteUseIds(List.of(primarySiteUseId, shipToSiteUseId));
        updateReq.setStatus(Status.ACTIVE);

        mockMvc.perform(put("/v1/sites/" + siteIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.siteName").value("Updated Site"))
                .andExpect(jsonPath("$.data.siteUses.length()").value(2));
    }

    @Test
    void deleteSite_whenOnlySiteExists_fails() throws Exception {
        SiteCreateRequest createReq = new SiteCreateRequest();
        createReq.setAccountId(accountId);
        createReq.setSiteName("The Only Site");
        createReq.setAddressLine1("123 St");
        createReq.setSiteUseIds(List.of(primarySiteUseId));
        
        MvcResult result = mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String siteIdStr = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asText();

        mockMvc.perform(delete("/v1/sites/" + siteIdStr))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Cannot delete the final site of an account. Each account must have at least one active site."));
    }

    @Test
    void deleteSite_whenOnlyPrimaryExists_fails() throws Exception {
        // Site 1 (Primary)
        SiteCreateRequest createReq1 = new SiteCreateRequest();
        createReq1.setAccountId(accountId);
        createReq1.setSiteName("Primary Site");
        createReq1.setAddressLine1("123 St");
        createReq1.setSiteUseIds(List.of(primarySiteUseId));
        
        MvcResult result1 = mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq1)))
                .andExpect(status().isCreated())
                .andReturn();

        String primarySiteIdStr = objectMapper.readTree(result1.getResponse().getContentAsString()).get("data").get("id").asText();

        // Site 2 (Non-primary)
        SiteCreateRequest createReq2 = new SiteCreateRequest();
        createReq2.setAccountId(accountId);
        createReq2.setSiteName("Secondary Site");
        createReq2.setAddressLine1("456 St");
        createReq2.setSiteUseIds(List.of(billToSiteUseId));
        
        mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq2)))
                .andExpect(status().isCreated());

        // Delete primary
        mockMvc.perform(delete("/v1/sites/" + primarySiteIdStr))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Cannot delete this site because it is the only PRIMARY site for the account."));
    }

    @Test
    void getSitesByAccount_returnsList() throws Exception {
        SiteCreateRequest createReq = new SiteCreateRequest();
        createReq.setAccountId(accountId);
        createReq.setSiteName("List Test Site");
        createReq.setAddressLine1("123 St");
        createReq.setSiteUseIds(List.of(primarySiteUseId));
        
        mockMvc.perform(post("/v1/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/sites/account/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].siteName").value("List Test Site"));
    }
}
