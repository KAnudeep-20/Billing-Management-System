package com.aibilling.catalog.service.impl;

import com.aibilling.catalog.dto.InventoryTransactionRequest;
import com.aibilling.catalog.dto.StockTransferRequest;
import com.aibilling.catalog.model.CatalogItem;
import com.aibilling.catalog.model.InventoryBalance;
import com.aibilling.catalog.model.InventoryTransaction;
import com.aibilling.catalog.model.InventoryTransactionType;
import com.aibilling.catalog.model.ItemUom;
import com.aibilling.catalog.model.Uom;
import com.aibilling.catalog.model.Warehouse;
import com.aibilling.catalog.repository.CatalogItemRepository;
import com.aibilling.catalog.repository.InventoryBalanceRepository;
import com.aibilling.catalog.repository.InventoryTransactionRepository;
import com.aibilling.catalog.repository.ItemUomRepository;
import com.aibilling.catalog.repository.UomRepository;
import com.aibilling.catalog.repository.WarehouseRepository;
import com.aibilling.catalog.service.InventoryTransactionService;
import com.aibilling.common.enums.Status;
import com.aibilling.exception.BusinessException;
import com.aibilling.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service implementation for managing Inventory Transactions.
 * All inventory balance changes flow through this service via ledger entries.
 */
@Service
@Transactional(readOnly = true)
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryTransactionServiceImpl.class);

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryBalanceRepository balanceRepository;
    private final CatalogItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final UomRepository uomRepository;
    private final ItemUomRepository itemUomRepository;

    public InventoryTransactionServiceImpl(InventoryTransactionRepository transactionRepository,
                                            InventoryBalanceRepository balanceRepository,
                                            CatalogItemRepository itemRepository,
                                            WarehouseRepository warehouseRepository,
                                            UomRepository uomRepository,
                                            ItemUomRepository itemUomRepository) {
        this.transactionRepository = transactionRepository;
        this.balanceRepository = balanceRepository;
        this.itemRepository = itemRepository;
        this.warehouseRepository = warehouseRepository;
        this.uomRepository = uomRepository;
        this.itemUomRepository = itemUomRepository;
    }

    @Override
    @Transactional
    public InventoryTransaction createTransaction(InventoryTransactionRequest request) {
        log.info("Creating inventory transaction type={}, item={}, warehouse={}",
                request.getTransactionType(), request.getItemId(), request.getWarehouseId());

        CatalogItem item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("CatalogItem", "id", request.getItemId().toString()));

        if (!item.getIsInventoryTracked()) {
            throw new BusinessException("Item '" + item.getItemName() + "' is not inventory-tracked. "
                    + "Inventory transactions are only allowed for inventory-tracked items.");
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId().toString()));

        Uom uom = uomRepository.findById(request.getUomId())
                .orElseThrow(() -> new ResourceNotFoundException("UOM", "id", request.getUomId().toString()));

        BigDecimal conversionFactor = resolveConversionFactor(item, uom);
        BigDecimal quantityInPrimaryUom = request.getQuantity()
                .multiply(conversionFactor)
                .setScale(4, RoundingMode.HALF_UP);

        // Get or create balance record
        InventoryBalance balance = balanceRepository
                .findByItemIdAndWarehouseIdAndStatus(item.getId(), warehouse.getId(), Status.ACTIVE)
                .orElseGet(() -> createNewBalance(item, warehouse));

        // Apply transaction to balance
        applyTransactionToBalance(balance, request.getTransactionType(), quantityInPrimaryUom);

        // Persist the transaction
        InventoryTransaction txn = new InventoryTransaction();
        txn.setItem(item);
        txn.setWarehouse(warehouse);
        txn.setTransactionDate(LocalDateTime.now());
        txn.setTransactionType(request.getTransactionType());
        txn.setReferenceType(request.getReferenceType());
        txn.setReferenceId(request.getReferenceId());
        txn.setQuantity(request.getQuantity());
        txn.setUom(uom);
        txn.setConversionFactor(conversionFactor);
        txn.setQuantityInPrimaryUOM(quantityInPrimaryUom);
        txn.setRemarks(request.getRemarks());
        txn.setStatus(Status.ACTIVE);

        balanceRepository.save(balance);
        InventoryTransaction saved = transactionRepository.save(txn);

        log.info("InventoryTransaction created with id={}, balance updated for item={}, warehouse={}",
                saved.getId(), item.getId(), warehouse.getId());
        return saved;
    }

    @Override
    @Transactional
    public void transferStock(StockTransferRequest request) {
        log.info("Transferring stock item={}, from={}, to={}, qty={}",
                request.getItemId(), request.getSourceWarehouseId(),
                request.getDestinationWarehouseId(), request.getQuantity());

        if (request.getSourceWarehouseId().equals(request.getDestinationWarehouseId())) {
            throw new BusinessException("Source and destination warehouses must be different.");
        }

        // Create TRANSFER_OUT from source
        InventoryTransactionRequest outRequest = new InventoryTransactionRequest();
        outRequest.setItemId(request.getItemId());
        outRequest.setWarehouseId(request.getSourceWarehouseId());
        outRequest.setTransactionType(InventoryTransactionType.TRANSFER_OUT);
        outRequest.setQuantity(request.getQuantity());
        outRequest.setUomId(request.getUomId());
        outRequest.setReferenceType("STOCK_TRANSFER");
        outRequest.setRemarks(request.getRemarks() != null ? request.getRemarks() : "Transfer out to warehouse");

        createTransaction(outRequest);

        // Create TRANSFER_IN to destination
        InventoryTransactionRequest inRequest = new InventoryTransactionRequest();
        inRequest.setItemId(request.getItemId());
        inRequest.setWarehouseId(request.getDestinationWarehouseId());
        inRequest.setTransactionType(InventoryTransactionType.TRANSFER_IN);
        inRequest.setQuantity(request.getQuantity());
        inRequest.setUomId(request.getUomId());
        inRequest.setReferenceType("STOCK_TRANSFER");
        inRequest.setRemarks(request.getRemarks() != null ? request.getRemarks() : "Transfer in from warehouse");

        createTransaction(inRequest);

        log.info("Stock transfer completed for item={}", request.getItemId());
    }

    @Override
    public Page<InventoryTransaction> getTransactionsByItem(UUID itemId, Pageable pageable) {
        return transactionRepository.findByItemIdAndStatus(itemId, Status.ACTIVE, pageable);
    }

    @Override
    public Page<InventoryTransaction> getTransactionsByWarehouse(UUID warehouseId, Pageable pageable) {
        return transactionRepository.findByWarehouseIdAndStatus(warehouseId, Status.ACTIVE, pageable);
    }

    @Override
    public Page<InventoryTransaction> getTransactionsByItemAndWarehouse(UUID itemId, UUID warehouseId, Pageable pageable) {
        return transactionRepository.findByItemIdAndWarehouseIdAndStatus(itemId, warehouseId, Status.ACTIVE, pageable);
    }

    // ==================== Private Helpers ====================

    private InventoryBalance createNewBalance(CatalogItem item, Warehouse warehouse) {
        InventoryBalance balance = new InventoryBalance();
        balance.setItem(item);
        balance.setWarehouse(warehouse);
        balance.setQuantityOnHand(BigDecimal.ZERO);
        balance.setReservedQty(BigDecimal.ZERO);
        balance.setAvailableQty(BigDecimal.ZERO);
        balance.setStatus(Status.ACTIVE);
        return balance;
    }

    private BigDecimal resolveConversionFactor(CatalogItem item, Uom uom) {
        // If the UOM matches the primary UOM, conversion factor is 1
        if (item.getPrimaryUom().getId().equals(uom.getId())) {
            return BigDecimal.ONE;
        }

        // Look up conversion factor from item_uoms
        return itemUomRepository.findByItemIdAndUomIdAndStatus(item.getId(), uom.getId(), Status.ACTIVE)
                .map(ItemUom::getConversionFactor)
                .orElseThrow(() -> new BusinessException(
                        "No UOM conversion found for item '" + item.getItemName()
                                + "' with UOM '" + uom.getCode() + "'. "
                                + "Please add a UOM mapping first."));
    }

    private void applyTransactionToBalance(InventoryBalance balance, InventoryTransactionType type, BigDecimal qtyInPrimary) {
        BigDecimal qoh = balance.getQuantityOnHand();
        BigDecimal reserved = balance.getReservedQty();

        switch (type) {
            case PURCHASE_RECEIPT:
            case SALES_RETURN:
            case TRANSFER_IN:
                balance.setQuantityOnHand(qoh.add(qtyInPrimary));
                break;

            case SALES_ISSUE:
            case PURCHASE_RETURN:
            case TRANSFER_OUT:
                BigDecimal newQoh = qoh.subtract(qtyInPrimary);
                if (newQoh.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("Insufficient stock. Available on-hand: " + qoh
                            + ", requested: " + qtyInPrimary);
                }
                balance.setQuantityOnHand(newQoh);
                break;

            case RESERVATION:
                BigDecimal available = qoh.subtract(reserved);
                if (qtyInPrimary.compareTo(available) > 0) {
                    throw new BusinessException("Insufficient available stock for reservation. "
                            + "Available: " + available + ", requested: " + qtyInPrimary);
                }
                balance.setReservedQty(reserved.add(qtyInPrimary));
                break;

            case RESERVATION_RELEASE:
                BigDecimal newReserved = reserved.subtract(qtyInPrimary);
                if (newReserved.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("Cannot release more than reserved quantity. "
                            + "Reserved: " + reserved + ", releasing: " + qtyInPrimary);
                }
                balance.setReservedQty(newReserved);
                break;

            case INVENTORY_ADJUSTMENT:
                // Adjustment can be positive or negative; quantity is always positive in request,
                // and the adjustment direction is implied by a positive adjustment (increase).
                // For decrease adjustments, the caller should use a separate mechanism or
                // pass negative quantity. Here we treat it as an increase.
                balance.setQuantityOnHand(qoh.add(qtyInPrimary));
                break;

            default:
                throw new BusinessException("Unsupported transaction type: " + type);
        }
    }
}
