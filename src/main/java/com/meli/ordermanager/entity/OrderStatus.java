package com.meli.ordermanager.entity;

/**
 * Enumeration representing the comprehensive lifecycle statuses of an order.
 * 
 * This enum defines all possible states an order can transition through from
 * initial creation to final completion or cancellation. Each status includes
 * a descriptive message for clear communication and audit purposes. The enum
 * supports the complete order fulfillment workflow in the e-commerce system.
 * 
 * Order Status Flow:
 * PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 *    ↓           ↓           ↓          ↓
 * CANCELLED   CANCELLED   CANCELLED  CANCELLED
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not yet processed.
     */
    PENDING("Order is pending processing"),
    
    /**
     * Order has been confirmed and is being prepared.
     */
    CONFIRMED("Order has been confirmed"),
    
    /**
     * Order is currently being processed.
     */
    PROCESSING("Order is being processed"),
    
    /**
     * Order has been shipped to the customer.
     */
    SHIPPED("Order has been shipped"),
    
    /**
     * Order has been delivered to the customer.
     */
    DELIVERED("Order has been delivered"),
    
    /**
     * Order has been cancelled by customer or system.
     */
    CANCELLED("Order has been cancelled");

    private final String description;

    /**
     * Constructor for OrderStatus enum.
     * 
     * @param description human-readable description of the status
     */
    OrderStatus(String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of the status.
     * 
     * @return the description of this order status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the status represents a completed order.
     * 
     * @return true if the order is delivered or cancelled, false otherwise
     */
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Checks if the status represents an active order.
     * 
     * @return true if the order is not completed, false otherwise
     */
    public boolean isActive() {
        return !isCompleted();
    }
}