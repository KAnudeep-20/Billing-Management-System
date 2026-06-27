package com.aibilling.entity;

import com.aibilling.common.enums.Status;
import com.aibilling.entity.dto.EntityCreateRequest;
import com.aibilling.entity.dto.OrganizationDetailsDto;
import com.aibilling.entity.dto.PersonDetailsDto;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.setup.model.EntityType;
import com.aibilling.setup.repository.EntityTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Entity CRUD endpoints and validation business rules.
 */
@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EntityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityTypeRepository entityTypeRepository;

    @Autowired
    private com.aibilling.entity.repository.EntityRepository entityRepository;

    @Autowired
    private com.aibilling.account.repository.AccountRepository accountRepository;

    @Autowired
    private com.aibilling.contact.repository.ContactRepository contactRepository;

    @Autowired
    private com.aibilling.site.repository.SiteRepository siteRepository;

    @Autowired
    private com.aibilling.site.repository.SiteUseMappingRepository siteUseMappingRepository;

    @Autowired
    private com.aibilling.relationship.repository.EntityRelationshipRepository entityRelationshipRepository;

    @BeforeEach
    void setUp() {








        // Seed customer entity type
        EntityType customer = new EntityType();
        customer.setCode("CUSTOMER");
        customer.setName("Customer");
        customer.setStatus(Status.ACTIVE);
        entityTypeRepository.save(customer);

        // Seed supplier entity type
        EntityType supplier = new EntityType();
        supplier.setCode("SUPPLIER");
        supplier.setName("Supplier");
        supplier.setStatus(Status.ACTIVE);
        entityTypeRepository.save(supplier);
    }

    @Test
    void createOrganization_withValidData_succeeds() throws Exception {
        OrganizationDetailsDto orgDetails = new OrganizationDetailsDto();
        orgDetails.setOrganizationName("Acme Corp");
        orgDetails.setTin("12-3456789");

        EntityCreateRequest request = new EntityCreateRequest();
        request.setEntityCategory(EntityCategory.ORGANIZATION);
        request.setEntityTypeCodes(List.of("CUSTOMER", "SUPPLIER"));
        request.setOrganizationDetails(orgDetails);

        mockMvc.perform(post("/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.entityCategory").value("ORGANIZATION"))
                .andExpect(jsonPath("$.data.organizationDetails.organizationName").value("Acme Corp"))
                .andExpect(jsonPath("$.data.organizationDetails.tin").value("12-3456789"));
    }

    @Test
    void createOrganization_withMissingTin_fails() throws Exception {
        OrganizationDetailsDto orgDetails = new OrganizationDetailsDto();
        orgDetails.setOrganizationName("Acme Corp");
        orgDetails.setTin(""); // blank

        EntityCreateRequest request = new EntityCreateRequest();
        request.setEntityCategory(EntityCategory.ORGANIZATION);
        request.setEntityTypeCodes(List.of("CUSTOMER"));
        request.setOrganizationDetails(orgDetails);

        mockMvc.perform(post("/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPerson_withValidData_succeeds() throws Exception {
        PersonDetailsDto personDetails = new PersonDetailsDto();
        personDetails.setFullName("John Doe");
        personDetails.setIdentificationType("SSN");
        personDetails.setIdentificationNumber("000-12-3456");

        EntityCreateRequest request = new EntityCreateRequest();
        request.setEntityCategory(EntityCategory.PERSON);
        request.setEntityTypeCodes(List.of("CUSTOMER"));
        request.setPersonDetails(personDetails);

        mockMvc.perform(post("/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.entityCategory").value("PERSON"))
                .andExpect(jsonPath("$.data.personDetails.fullName").value("John Doe"))
                .andExpect(jsonPath("$.data.personDetails.identificationType").value("SSN"))
                .andExpect(jsonPath("$.data.personDetails.identificationNumber").value("000-12-3456"));
    }

    @Test
    void createPerson_withMissingFullName_fails() throws Exception {
        PersonDetailsDto personDetails = new PersonDetailsDto();
        personDetails.setFullName(""); // blank
        personDetails.setIdentificationType("SSN");
        personDetails.setIdentificationNumber("000-12-3456");

        EntityCreateRequest request = new EntityCreateRequest();
        request.setEntityCategory(EntityCategory.PERSON);
        request.setEntityTypeCodes(List.of("CUSTOMER"));
        request.setPersonDetails(personDetails);

        mockMvc.perform(post("/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
