package com.aibilling.catalog;

import com.aibilling.catalog.dto.*;
import com.aibilling.catalog.model.InventoryTransactionType;
import com.aibilling.catalog.repository.*;
import com.aibilling.common.enums.Status;
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
public class CatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UomRepository uomRepository;

    @Autowired
    private CatalogCategoryRepository categoryRepository;

    @Autowired
    private CatalogItemRepository itemRepository;

    @Autowired
    private ItemUomRepository itemUomRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryBalanceRepository balanceRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    private UUID primaryUomId;
    private UUID secondaryUomId;
    private UUID rootCategoryId;
    private UUID leafCategoryId;
    private UUID warehouse1Id;
    private UUID warehouse2Id;

    @BeforeEach
    void setUp() throws Exception {
        // Clear repositories (not strictly needed with AFTER_EACH_TEST_METHOD, but good hygiene)
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();
        itemUomRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        uomRepository.deleteAll();
        warehouseRepository.deleteAll();

        // 1. Create primary UOM (EACH)
        UomRequest uomReq1 = new UomRequest("EACH", "Each unit");
        MvcResult uomRes1 = mockMvc.perform(post("/v1/uoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uomReq1)))
                .andExpect(status().isCreated())
                .andReturn();
        String uomBody1 = uomRes1.getResponse().getContentAsString();
        primaryUomId = UUID.fromString(objectMapper.readTree(uomBody1).get("data").get("id").asText());

        // 2. Create secondary UOM (BOX)
        UomRequest uomReq2 = new UomRequest("BOX", "Box unit");
        MvcResult uomRes2 = mockMvc.perform(post("/v1/uoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uomReq2)))
                .andExpect(status().isCreated())
                .andReturn();
        String uomBody2 = uomRes2.getResponse().getContentAsString();
        secondaryUomId = UUID.fromString(objectMapper.readTree(uomBody2).get("data").get("id").asText());

        // 3. Create root category (HARDWARE)
        CatalogCategoryRequest catReq1 = CatalogCategoryRequest.builder()
                .code("HARDWARE")
                .name("Hardware Category")
                .description("Root Hardware Category")
                .build();
        MvcResult catRes1 = mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq1)))
                .andExpect(status().isCreated())
                .andReturn();
        String catBody1 = catRes1.getResponse().getContentAsString();
        rootCategoryId = UUID.fromString(objectMapper.readTree(catBody1).get("data").get("id").asText());

        // 4. Create leaf category (LAPTOPS) with parent HARDWARE
        CatalogCategoryRequest catReq2 = CatalogCategoryRequest.builder()
                .code("LAPTOPS")
                .name("Laptops Category")
                .description("Sub category laptops")
                .parentCategoryId(rootCategoryId)
                .build();
        MvcResult catRes2 = mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq2)))
                .andExpect(status().isCreated())
                .andReturn();
        String catBody2 = catRes2.getResponse().getContentAsString();
        leafCategoryId = UUID.fromString(objectMapper.readTree(catBody2).get("data").get("id").asText());

        // 5. Create warehouses
        WarehouseRequest whReq1 = new WarehouseRequest("WH-001", "Primary WH", "123 Main St");
        MvcResult whRes1 = mockMvc.perform(post("/v1/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(whReq1)))
                .andExpect(status().isCreated())
                .andReturn();
        String whBody1 = whRes1.getResponse().getContentAsString();
        warehouse1Id = UUID.fromString(objectMapper.readTree(whBody1).get("data").get("id").asText());

        WarehouseRequest whReq2 = new WarehouseRequest("WH-002", "Secondary WH", "456 Side St");
        MvcResult whRes2 = mockMvc.perform(post("/v1/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(whReq2)))
                .andExpect(status().isCreated())
                .andReturn();
        String whBody2 = whRes2.getResponse().getContentAsString();
        warehouse2Id = UUID.fromString(objectMapper.readTree(whBody2).get("data").get("id").asText());
    }

    @Test
    void testCategoryHierarchyRules() throws Exception {
        // Rule: Only leaf categories can have items assigned.
        // Let's create an item under Root Category (HARDWARE) which is a non-leaf category (since LAPTOPS exists).
        CatalogItemRequest itemReq = CatalogItemRequest.builder()
                .itemNumber("LAP-001")
                .itemName("Generic Laptop")
                .categoryId(rootCategoryId) // ROOT which is parent of LAPTOPS
                .primaryUomId(primaryUomId)
                .listPrice(new BigDecimal("999.99"))
                .isStocked(true)
                .isInventoryTracked(true)
                .build();

        mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemReq)))
                .andExpect(status().isUnprocessableEntity()) // BusinessException mapping
                .andExpect(jsonPath("$.message").value("Items can only be assigned to leaf categories. Category 'Hardware Category' has sub-categories."));

        // Let's create an item under leaf category (LAPTOPS) -> should succeed
        itemReq.setCategoryId(leafCategoryId);
        MvcResult itemRes = mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemReq)))
                .andExpect(status().isCreated())
                .andReturn();
        String itemBody = itemRes.getResponse().getContentAsString();
        UUID itemId = UUID.fromString(objectMapper.readTree(itemBody).get("data").get("id").asText());

        // Now if we try to create a sub-category under LAPTOPS which now has an item, it should fail
        CatalogCategoryRequest subCatReq = CatalogCategoryRequest.builder()
                .code("ULTRABOOKS")
                .name("Ultrabooks sub category")
                .parentCategoryId(leafCategoryId)
                .build();
        mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subCatReq)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Cannot add sub-category to category 'Laptops Category' because it already has catalog items assigned. Only leaf categories can hold items."));
    }

    @Test
    void testItemBehaviorFlagValidation() throws Exception {
        // stocked and service item cannot be true at the same time
        CatalogItemRequest invalidItemReq = CatalogItemRequest.builder()
                .itemNumber("SRV-001")
                .itemName("Invalid Item")
                .categoryId(leafCategoryId)
                .primaryUomId(primaryUomId)
                .listPrice(new BigDecimal("100.00"))
                .isStocked(true)
                .isService(true)
                .build();

        mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemReq)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("A stocked item cannot be a service item."));

        // inventory-tracked requires stocked
        CatalogItemRequest invalidItemReq2 = CatalogItemRequest.builder()
                .itemNumber("SRV-002")
                .itemName("Invalid Item 2")
                .categoryId(leafCategoryId)
                .primaryUomId(primaryUomId)
                .listPrice(new BigDecimal("100.00"))
                .isStocked(false)
                .isInventoryTracked(true)
                .build();

        mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemReq2)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("An inventory-tracked item must also be stocked."));
    }

    @Test
    void testItemSecondaryUomMapping() throws Exception {
        // Create valid item under LAPTOPS
        CatalogItemRequest itemReq = CatalogItemRequest.builder()
                .itemNumber("LAP-100")
                .itemName("Developer Laptop")
                .categoryId(leafCategoryId)
                .primaryUomId(primaryUomId)
                .listPrice(new BigDecimal("1200.00"))
                .isStocked(true)
                .isInventoryTracked(true)
                .build();

        MvcResult itemRes = mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemReq)))
                .andExpect(status().isCreated())
                .andReturn();
        String itemBody = itemRes.getResponse().getContentAsString();
        UUID itemId = UUID.fromString(objectMapper.readTree(itemBody).get("data").get("id").asText());

        // Create UOM mapping to BOX (conversion factor 10)
        ItemUomRequest itemUomReq = ItemUomRequest.builder()
                .uomId(secondaryUomId)
                .conversionFactor(new BigDecimal("10.000000"))
                .isDefault(false)
                .build();

        mockMvc.perform(post("/v1/items/" + itemId + "/uoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUomReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.uomCode").value("BOX"))
                .andExpect(jsonPath("$.data.conversionFactor").value(10.0));
    }

    @Test
    void testInventoryLedgerAndStockBalances() throws Exception {
        // Create an inventory tracked item
        CatalogItemRequest itemReq = CatalogItemRequest.builder()
                .itemNumber("LAP-200")
                .itemName("Office Laptop")
                .categoryId(leafCategoryId)
                .primaryUomId(primaryUomId)
                .listPrice(new BigDecimal("800.00"))
                .isStocked(true)
                .isInventoryTracked(true)
                .build();

        MvcResult itemRes = mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemReq)))
                .andExpect(status().isCreated())
                .andReturn();
        String itemBody = itemRes.getResponse().getContentAsString();
        UUID itemId = UUID.fromString(objectMapper.readTree(itemBody).get("data").get("id").asText());

        // Add BOX mapping
        ItemUomRequest itemUomReq = ItemUomRequest.builder()
                .uomId(secondaryUomId)
                .conversionFactor(new BigDecimal("10.000000"))
                .build();
        mockMvc.perform(post("/v1/items/" + itemId + "/uoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUomReq)))
                .andExpect(status().isCreated());

        // 1. Receive stock: PURCHASE_RECEIPT of 5 BOXes in WH-001 (primary UOM conversion: 50 EACH)
        InventoryTransactionRequest receiveReq = InventoryTransactionRequest.builder()
                .itemId(itemId)
                .warehouseId(warehouse1Id)
                .transactionType(InventoryTransactionType.PURCHASE_RECEIPT)
                .quantity(new BigDecimal("5.0000"))
                .uomId(secondaryUomId) // BOX
                .remarks("Initial purchase receipt")
                .build();

        mockMvc.perform(post("/v1/inventory/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiveReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantityInPrimaryUOM").value(50.0000));

        // 2. Query Balance in WH-001
        mockMvc.perform(get("/v1/inventory/balances")
                        .param("itemId", itemId.toString())
                        .param("warehouseId", warehouse1Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].quantityOnHand").value(50.0000))
                .andExpect(jsonPath("$.data[0].reservedQty").value(0.0000))
                .andExpect(jsonPath("$.data[0].availableQty").value(50.0000));

        // 3. Reserve 15 EACH
        InventoryTransactionRequest reserveReq = InventoryTransactionRequest.builder()
                .itemId(itemId)
                .warehouseId(warehouse1Id)
                .transactionType(InventoryTransactionType.RESERVATION)
                .quantity(new BigDecimal("15.0000"))
                .uomId(primaryUomId)
                .remarks("Customer order reservation")
                .build();

        mockMvc.perform(post("/v1/inventory/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveReq)))
                .andExpect(status().isCreated());

        // Query Balance again
        mockMvc.perform(get("/v1/inventory/balances")
                        .param("itemId", itemId.toString())
                        .param("warehouseId", warehouse1Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantityOnHand").value(50.0000))
                .andExpect(jsonPath("$.data[0].reservedQty").value(15.0000))
                .andExpect(jsonPath("$.data[0].availableQty").value(35.0000));

        // 4. Over-reserve (requesting 40 more, only 35 available) -> should fail
        InventoryTransactionRequest overReserveReq = InventoryTransactionRequest.builder()
                .itemId(itemId)
                .warehouseId(warehouse1Id)
                .transactionType(InventoryTransactionType.RESERVATION)
                .quantity(new BigDecimal("40.0000"))
                .uomId(primaryUomId)
                .remarks("Over reservation")
                .build();

        mockMvc.perform(post("/v1/inventory/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overReserveReq)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Insufficient available stock for reservation. Available: 35.0000, requested: 40.0000"));

        // 5. Transfer stock: 20 EACH from WH-001 to WH-002
        StockTransferRequest transferReq = StockTransferRequest.builder()
                .itemId(itemId)
                .sourceWarehouseId(warehouse1Id)
                .destinationWarehouseId(warehouse2Id)
                .quantity(new BigDecimal("20.0000"))
                .uomId(primaryUomId)
                .remarks("Inter WH transfer")
                .build();

        mockMvc.perform(post("/v1/inventory/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferReq)))
                .andExpect(status().isCreated());

        // Check source WH-001 balance: on-hand should be 30 (50 - 20)
        mockMvc.perform(get("/v1/inventory/balances")
                        .param("itemId", itemId.toString())
                        .param("warehouseId", warehouse1Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantityOnHand").value(30.0000))
                .andExpect(jsonPath("$.data[0].reservedQty").value(15.0000))
                .andExpect(jsonPath("$.data[0].availableQty").value(15.0000));

        // Check destination WH-002 balance: on-hand should be 20
        mockMvc.perform(get("/v1/inventory/balances")
                        .param("itemId", itemId.toString())
                        .param("warehouseId", warehouse2Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantityOnHand").value(20.0000))
                .andExpect(jsonPath("$.data[0].reservedQty").value(0.0000))
                .andExpect(jsonPath("$.data[0].availableQty").value(20.0000));
    }
}
