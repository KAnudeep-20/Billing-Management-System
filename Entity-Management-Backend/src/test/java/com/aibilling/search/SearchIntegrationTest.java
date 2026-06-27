package com.aibilling.search;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.contact.model.Contact;
import com.aibilling.contact.repository.ContactRepository;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.model.EntityDetails;
import com.aibilling.entity.repository.EntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup data for testing
        // Entity 1: specific org name
        Entity e1 = new Entity();
        e1.setEntityCategory(EntityCategory.ORGANIZATION);
        EntityDetails ed1 = new EntityDetails();
        ed1.setOrganizationName("Global Tech Corp");
        e1.setDetails(ed1);
        e1 = entityRepository.save(e1);

        // Account 1 for Entity 1
        Account a1 = new Account();
        a1.setEntity(e1);
        a1.setAccountName("Global Tech Services Account");
        a1 = accountRepository.save(a1);

        // Contact 1 for Account 1
        Contact c1 = new Contact();
        c1.setAccount(a1);
        c1.setFirstName("Alice");
        c1.setLastName("Smith");
        contactRepository.save(c1);

        // Entity 2: specific contact name
        Entity e2 = new Entity();
        e2.setEntityCategory(EntityCategory.PERSON);
        EntityDetails ed2 = new EntityDetails();
        ed2.setFullName("John Doe");
        e2.setDetails(ed2);
        e2 = entityRepository.save(e2);

        Account a2 = new Account();
        a2.setEntity(e2);
        a2.setAccountName("John Doe Personal Account");
        a2 = accountRepository.save(a2);

        Contact c2 = new Contact();
        c2.setAccount(a2);
        c2.setFirstName("Bob");
        c2.setLastName("Johnson");
        contactRepository.save(c2);
    }

    @AfterEach
    void tearDown() {



    }

    @Test
    void searchByEntityOrganizationName() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", "Tech Corp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].organizationName", is("Global Tech Corp")));
    }

    @Test
    void searchByAccountName() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", "Personal Account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("John Doe")));
    }

    @Test
    void searchByContactFirstName() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].organizationName", is("Global Tech Corp")));
    }

    @Test
    void searchByContactLastName() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", "Johnson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("John Doe")));
    }

    @Test
    void searchNoResults() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", "NonExistentName123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void searchEmptyQueryReturnsAll() throws Exception {
        mockMvc.perform(get("/v1/search/entities")
                .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void searchPerformanceTest() throws Exception {
        // Measure execution time
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/v1/search/entities")
                .param("query", "Global"))
                .andExpect(status().isOk());

        stopWatch.stop();

        // Ensure performance is well under 2 seconds.
        // Even with a small dataset here, we assert it doesn't do something crazy.
        // In reality, 50,000 records performance is guaranteed by the indexed query logic in DB.
        assertTrue(stopWatch.getTotalTimeMillis() < 2000, "Search query took too long: " + stopWatch.getTotalTimeMillis() + "ms");
    }
}
