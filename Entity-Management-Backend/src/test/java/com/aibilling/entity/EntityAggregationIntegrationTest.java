package com.aibilling.entity;

import com.aibilling.account.model.Account;
import com.aibilling.account.repository.AccountRepository;
import com.aibilling.contact.model.Contact;
import com.aibilling.contact.repository.ContactRepository;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.relationship.model.EntityRelationship;
import com.aibilling.relationship.repository.EntityRelationshipRepository;
import com.aibilling.setup.model.RelationshipType;
import com.aibilling.setup.repository.RelationshipTypeRepository;
import com.aibilling.site.model.Site;
import com.aibilling.site.repository.SiteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EntityAggregationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityRelationshipRepository relationshipRepository;

    @Autowired
    private RelationshipTypeRepository relationshipTypeRepository;

    private UUID rootEntityId;

    @BeforeEach
    void setUp() {
        Entity entity1 = new Entity();
        entity1.setEntityCategory(EntityCategory.ORGANIZATION);
        entity1 = entityRepository.save(entity1);
        rootEntityId = entity1.getId();

        Entity entity2 = new Entity();
        entity2.setEntityCategory(EntityCategory.ORGANIZATION);
        entity2 = entityRepository.save(entity2);

        RelationshipType relType = new RelationshipType();
        relType.setCode("PARENT");
        relType.setName("Parent Of");
        relType = relationshipTypeRepository.save(relType);

        EntityRelationship rel = new EntityRelationship();
        rel.setSubjectEntity(entity1);
        rel.setObjectEntity(entity2);
        rel.setRelationshipType(relType);
        relationshipRepository.save(rel);

        Account account1 = new Account();
        account1.setEntity(entity1);
        account1.setAccountName("Account 1");
        account1 = accountRepository.save(account1);

        Account account2 = new Account();
        account2.setEntity(entity1);
        account2.setAccountName("Account 2");
        account2 = accountRepository.save(account2);

        Site site1 = new Site();
        site1.setAccount(account1);
        site1.setSiteName("Main Site 1");
        site1.setAddressLine1("123 Main St");
        siteRepository.save(site1);

        Site site2 = new Site();
        site2.setAccount(account2);
        site2.setSiteName("Branch Site 2");
        site2.setAddressLine1("456 Elm St");
        siteRepository.save(site2);

        Contact contact1 = new Contact();
        contact1.setAccount(account1);
        contact1.setFirstName("Alice");
        contactRepository.save(contact1);

        Contact contact2 = new Contact();
        contact2.setAccount(account2);
        contact2.setFirstName("Bob");
        contactRepository.save(contact2);
    }

    @AfterEach
    void tearDown() {






    }

    @Test
    void getCompleteEntityDetails_ShouldReturnHierarchy() throws Exception {
        mockMvc.perform(get("/v1/entities/" + rootEntityId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.entitySummary.id", is(rootEntityId.toString())))
                .andExpect(jsonPath("$.data.relationships", hasSize(1)))
                .andExpect(jsonPath("$.data.relationships[0].relationshipTypeName", is("Parent Of")))
                .andExpect(jsonPath("$.data.accounts", hasSize(2)))
                // Verify Account 1 mapping
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 1')].sites", hasSize(1)))
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 1')].sites[0].addressLine1", hasSize(1)))
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 1')].contacts", hasSize(1)))
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 1')].contacts[0].firstName", hasSize(1)))
                // Verify Account 2 mapping
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 2')].sites", hasSize(1)))
                .andExpect(jsonPath("$.data.accounts[?(@.accountName=='Account 2')].contacts", hasSize(1)));
    }
}
