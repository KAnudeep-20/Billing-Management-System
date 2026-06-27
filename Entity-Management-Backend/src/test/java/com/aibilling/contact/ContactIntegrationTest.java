package com.aibilling.contact;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.contact.dto.ContactCreateRequest;
import com.aibilling.contact.dto.ContactUpdateRequest;
import com.aibilling.contact.repository.ContactRepository;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.setup.model.ContactType;
import com.aibilling.setup.repository.ContactTypeRepository;
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
public class ContactIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private ContactTypeRepository contactTypeRepository;

    @Autowired
    private com.aibilling.site.repository.SiteRepository siteRepository;

    @Autowired
    private com.aibilling.site.repository.SiteUseMappingRepository siteUseMappingRepository;

    @Autowired
    private com.aibilling.relationship.repository.EntityRelationshipRepository entityRelationshipRepository;

    private UUID accountId;
    private UUID contactTypeId;

    @BeforeEach
    void setUp() {








        // Seed ContactType
        ContactType ct = new ContactType();
        ct.setCode("PRIMARY");
        ct.setName("Primary");
        ct = contactTypeRepository.save(ct);
        contactTypeId = ct.getId();

        // Seed Entity
        Entity entity = new Entity();
        entity.setEntityCategory(EntityCategory.ORGANIZATION);
        entity = entityRepository.save(entity);

        // Seed Account (directly, bypassing service to avoid auto placeholder for simpler tests)
        Account account = new Account();
        account.setEntity(entity);
        account.setAccountName("Test Account");
        account = accountRepository.save(account);
        accountId = account.getId();
    }

    @Test
    void createContact_succeeds() throws Exception {
        ContactCreateRequest request = new ContactCreateRequest();
        request.setAccountId(accountId);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+1-555-1234");
        request.setRole("Manager");
        request.setDesignation("VP Engineering");
        request.setContactTypeId(contactTypeId);

        mockMvc.perform(post("/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.phone").value("+1-555-1234"))
                .andExpect(jsonPath("$.data.role").value("Manager"))
                .andExpect(jsonPath("$.data.designation").value("VP Engineering"))
                .andExpect(jsonPath("$.data.placeholder").value(false))
                .andExpect(jsonPath("$.data.contactType.code").value("PRIMARY"));
    }

    @Test
    void listContactsByAccount_returnsMultiple() throws Exception {
        // Create contact 1
        ContactCreateRequest req1 = new ContactCreateRequest();
        req1.setAccountId(accountId);
        req1.setFirstName("Alice");
        mockMvc.perform(post("/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // Create contact 2
        ContactCreateRequest req2 = new ContactCreateRequest();
        req2.setAccountId(accountId);
        req2.setFirstName("Bob");
        mockMvc.perform(post("/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/contacts/account/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void updateContact_succeeds() throws Exception {
        // Create a contact
        ContactCreateRequest createReq = new ContactCreateRequest();
        createReq.setAccountId(accountId);
        createReq.setFirstName("Jane");
        MvcResult result = mockMvc.perform(post("/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String contactIdStr = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Update it
        ContactUpdateRequest updateReq = new ContactUpdateRequest();
        updateReq.setFirstName("Jane Updated");
        updateReq.setLastName("Smith");
        updateReq.setEmail("jane@example.com");
        updateReq.setStatus(Status.ACTIVE);

        mockMvc.perform(put("/v1/contacts/" + contactIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Jane Updated"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"));
    }

    @Test
    void deleteContact_succeeds() throws Exception {
        ContactCreateRequest createReq = new ContactCreateRequest();
        createReq.setAccountId(accountId);
        createReq.setFirstName("Delete Me");
        MvcResult result = mockMvc.perform(post("/v1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String contactIdStr = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("id").asText();

        mockMvc.perform(delete("/v1/contacts/" + contactIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void placeholderContact_autoCreated_onAccountCreation() throws Exception {
        // Create an account VIA the Account API (which triggers auto-placeholder)
        String accountPayload = """
                {
                    "entityId": "%s",
                    "accountName": "Account With Auto Placeholder"
                }
                """.formatted(entityRepository.findAll().get(0).getId().toString());

        MvcResult result = mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String newAccountIdStr = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // List contacts for the newly created account — should have 1 placeholder
        mockMvc.perform(get("/v1/contacts/account/" + newAccountIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("Missing Information"))
                .andExpect(jsonPath("$.data[0].placeholder").value(true));
    }

    @Test
    void updatePlaceholderContact_marksAsNonPlaceholder() throws Exception {
        // Create account via API to trigger placeholder
        String accountPayload = """
                {
                    "entityId": "%s",
                    "accountName": "Account For Placeholder Update"
                }
                """.formatted(entityRepository.findAll().get(0).getId().toString());

        MvcResult accountResult = mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String newAccountIdStr = objectMapper.readTree(accountResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // Get the placeholder contact
        MvcResult contactsResult = mockMvc.perform(get("/v1/contacts/account/" + newAccountIdStr))
                .andExpect(status().isOk())
                .andReturn();

        String placeholderIdStr = objectMapper.readTree(contactsResult.getResponse().getContentAsString())
                .get("data").get(0).get("id").asText();

        // Update the placeholder with real info
        ContactUpdateRequest updateReq = new ContactUpdateRequest();
        updateReq.setFirstName("Real");
        updateReq.setLastName("Person");
        updateReq.setEmail("real@example.com");
        updateReq.setStatus(Status.ACTIVE);

        mockMvc.perform(put("/v1/contacts/" + placeholderIdStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Real"))
                .andExpect(jsonPath("$.data.placeholder").value(false));
    }
}
