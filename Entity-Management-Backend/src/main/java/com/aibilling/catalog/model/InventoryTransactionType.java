package com.aibilling.catalog.model;

/**
 * Enumeration of supported Inventory Transaction types.
 */
public enum InventoryTransactionType {
    
    /** Increase QuantityOnHand. */
    PURCHASE_RECEIPT,

    /** Decrease QuantityOnHand. If for a reservation, also decrease ReservedQty. */
    SALES_ISSUE,

    /** Decrease QuantityOnHand. */
    PURCHASE_RETURN,

    /** Increase QuantityOnHand. */
    SALES_RETURN,

    /** Decrease QuantityOnHand from source warehouse. */
    TRANSFER_OUT,

    /** Increase QuantityOnHand to destination warehouse. */
    TRANSFER_IN,

    /** Increase ReservedQty (does not affect QuantityOnHand). */
    RESERVATION,

    /** Decrease ReservedQty (does not affect QuantityOnHand). */
    RESERVATION_RELEASE,

    /** Increase or Decrease QuantityOnHand directly. */
    INVENTORY_ADJUSTMENT

}
