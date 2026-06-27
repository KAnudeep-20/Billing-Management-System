package com.aibilling.relationship;

import com.aibilling.common.enums.Status;
import com.aibilling.entity.model.Entity;
import com.aibilling.entity.model.EntityCategory;
import com.aibilling.entity.repository.EntityRepository;
import com.aibilling.relationship.dto.EntityRelationshipCreateRequest;
import com.aibilling.relationship.repository.EntityRelationshipRepository;
import com.aibilling.setup.model.RelationshipType;
import com.aibilling.setup.repository.RelationshipTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EntityRelationshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityRelationshipRepository entityRelationshipRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private RelationshipTypeRepository relationshipTypeRepository;

    @Autowired
    private com.aibilling.account.repository.AccountRepository accountRepository;

    @Autowired
    private com.aibilling.contact.repository.ContactRepository contactRepository;

    @Autowired
    private com.aibilling.site.repository.SiteRepository siteRepository;

    @Autowired
    private com.aibilling.site.repository.SiteUseMappingRepository siteUseMappingRepository;

    private UUID subjectId;
    private UUID objectId;
    private UUID relationshipTypeId;

    @BeforeEach
    void setUp() {
        // Correct order deletion to prevent constraint violations








        // Seed Subject Entity
        Entity subject = new Entity();
        subject.setEntityCategory(EntityCategory.ORGANIZATION);
        subject.setStatus(Status.ACTIVE);
        subject = entityRepository.save(subject);
        subjectId = subject.getId();

        // Seed Object Entity
        Entity object = new Entity();
        object.setEntityCategory(EntityCategory.ORGANIZATION);
        object.setStatus(Status.ACTIVE);
        object = entityRepository.save(object);
        objectId = object.getId();

        // Seed RelationshipType
        RelationshipType type = new RelationshipType();
        type.setCode("PARENT_OF");
        type.setName("Parent Of");
        type.setStatus(Status.ACTIVE);
        type = relationshipTypeRepository.save(type);
        relationshipTypeId = type.getId();
    }

    @Test
    void createRelationship_succeeds() throws Exception {
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(objectId);

        mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectEntityId").value(subjectId.toString()))
                .andExpect(jsonPath("$.data.relationshipTypeId").value(relationshipTypeId.toString()))
                .andExpect(jsonPath("$.data.objectEntityId").value(objectId.toString()))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void createRelationship_selfReferencing_fails() throws Exception {
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(subjectId); // Same entity

        mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity()) // 422
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Self-referencing relationships are not allowed."));
    }

    @Test
    void queryRelationships_bySubject_returnsMatches() throws Exception {
        // Create one relationship first
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(objectId);

        mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Query by subject
        mockMvc.perform(get("/v1/entity-relationships?subjectId=" + subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].subjectEntityId").value(subjectId.toString()))
                .andExpect(jsonPath("$.data[0].objectEntityId").value(objectId.toString()));

        // Query by different subject
        mockMvc.perform(get("/v1/entity-relationships?subjectId=" + objectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void queryRelationships_byObject_returnsMatches() throws Exception {
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(objectId);

        mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Query by object
        mockMvc.perform(get("/v1/entity-relationships?objectId=" + objectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].objectEntityId").value(objectId.toString()));
    }

    @Test
    void queryRelationships_byEitherEntity_returnsMatches() throws Exception {
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(objectId);

        mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Query by entityId matching subject
        mockMvc.perform(get("/v1/entity-relationships?entityId=" + subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // Query by entityId matching object
        mockMvc.perform(get("/v1/entity-relationships?entityId=" + objectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void deleteRelationship_succeeds() throws Exception {
        EntityRelationshipCreateRequest request = new EntityRelationshipCreateRequest();
        request.setSubjectEntityId(subjectId);
        request.setRelationshipTypeId(relationshipTypeId);
        request.setObjectEntityId(objectId);

        String response = mockMvc.perform(post("/v1/entity-relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("data").get("id").asText();

        // Delete relationship
        mockMvc.perform(delete("/v1/entity-relationships/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify it is no longer listed as active
        mockMvc.perform(get("/v1/entity-relationships?entityId=" + subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
