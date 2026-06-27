package com.aibilling.account;

import com.aibilling.account.dto.AccountCreateRequest;
import com.aibilling.account.dto.AccountUpdateRequest;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.setup.model.BillingCycle;
import com.aibilling.setup.model.PaymentTerm;
import com.aibilling.setup.repository.BillingCycleRepository;
import com.aibilling.setup.repository.PaymentTermRepository;
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

import java.math.BigDecimal;
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
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private PaymentTermRepository paymentTermRepository;

    @Autowired
    private BillingCycleRepository billingCycleRepository;

    @Autowired
    private com.aibilling.contact.repository.ContactRepository contactRepository;

    @Autowired
    private com.aibilling.site.repository.SiteRepository siteRepository;

    @Autowired
    private com.aibilling.site.repository.SiteUseMappingRepository siteUseMappingRepository;

    @Autowired
    private com.aibilling.relationship.repository.EntityRelationshipRepository entityRelationshipRepository;

    private UUID entityId;
    private UUID paymentTermId;
    private UUID billingCycleId;

    @BeforeEach
    void setUp() {









        // Seed PaymentTerm
        PaymentTerm pt = new PaymentTerm();
        pt.setCode("NET_15");
        pt.setName("Net 15");
        pt.setDaysDue(15);
        pt = paymentTermRepository.save(pt);
        paymentTermId = pt.getId();

        // Seed BillingCycle
        BillingCycle bc = new BillingCycle();
        bc.setCode("MONTHLY");
        bc.setName("Monthly");
        bc = billingCycleRepository.save(bc);
        billingCycleId = bc.getId();

        // Seed Entity
        Entity entity = new Entity();
        entity.setEntityCategory(EntityCategory.ORGANIZATION);
        entity = entityRepository.save(entity);
        entityId = entity.getId();
    }

    @Test
    void createAccount_withValidData_succeeds() throws Exception {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setEntityId(entityId);
        request.setAccountName("Main Corporate Account");
        request.setNatureOfBusiness("Software Development");
        request.setCreditLimit(new BigDecimal("50000.00"));
        request.setPaymentTermId(paymentTermId);
        request.setBillingCycleId(billingCycleId);
        request.setCreditClassification("Class A");
        request.setCreditRisk("Low Risk");

        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountName").value("Main Corporate Account"))
                .andExpect(jsonPath("$.data.entityId").value(entityId.toString()))
                .andExpect(jsonPath("$.data.creditLimit").value(50000.00))
                .andExpect(jsonPath("$.data.paymentTerm.id").value(paymentTermId.toString()))
                .andExpect(jsonPath("$.data.billingCycle.id").value(billingCycleId.toString()));
    }

    @Test
    void getAccountsByEntity_returnsList() throws Exception {
        // Create an account directly
        AccountCreateRequest request = new AccountCreateRequest();
        request.setEntityId(entityId);
        request.setAccountName("Test Account 1");
        mockMvc.perform(post("/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/accounts/entity/" + entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].accountName").value("Test Account 1"));
    }

    @Test
    void updateAccount_withValidData_succeeds() throws Exception {
        // Create an account
        AccountCreateRequest createReq = new AccountCreateRequest();
        createReq.setEntityId(entityId);
        createReq.setAccountName("Initial Account");
        MvcResult result = mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accountIdStr = objectMapper.readTree(responseBody).get("data").get("id").asText();
        UUID accountId = UUID.fromString(accountIdStr);

        // Update it
        AccountUpdateRequest updateReq = new AccountUpdateRequest();
        updateReq.setAccountName("Updated Account Name");
        updateReq.setStatus(Status.ACTIVE);
        updateReq.setCreditLimit(new BigDecimal("10000.00"));
        
        mockMvc.perform(put("/v1/accounts/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountName").value("Updated Account Name"))
                .andExpect(jsonPath("$.data.creditLimit").value(10000.00));
    }

    @Test
    void deleteAccount_whenOnlyOneExists_failsWithBusinessException() throws Exception {
        // Create 1 account
        AccountCreateRequest createReq = new AccountCreateRequest();
        createReq.setEntityId(entityId);
        createReq.setAccountName("The Only Account");
        MvcResult result = mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accountIdStr = objectMapper.readTree(responseBody).get("data").get("id").asText();

        // Attempt to delete it
        mockMvc.perform(delete("/v1/accounts/" + accountIdStr))
                .andExpect(status().isUnprocessableEntity()) // Assuming BusinessException maps to 422 Unprocessable Entity
                .andExpect(jsonPath("$.message").value("Cannot delete the final account of an entity. Each entity must have at least one active account."));
    }

    @Test
    void deleteAccount_whenMultipleExist_succeeds() throws Exception {
        // Create Account 1
        AccountCreateRequest createReq1 = new AccountCreateRequest();
        createReq1.setEntityId(entityId);
        createReq1.setAccountName("Account 1");
        mockMvc.perform(post("/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq1)));

        // Create Account 2
        AccountCreateRequest createReq2 = new AccountCreateRequest();
        createReq2.setEntityId(entityId);
        createReq2.setAccountName("Account 2");
        MvcResult result2 = mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq2)))
                .andReturn();

        String responseBody = result2.getResponse().getContentAsString();
        String accountIdStr = objectMapper.readTree(responseBody).get("data").get("id").asText();

        // Delete Account 2
        mockMvc.perform(delete("/v1/accounts/" + accountIdStr))
                .andExpect(status().isOk());
    }
}
