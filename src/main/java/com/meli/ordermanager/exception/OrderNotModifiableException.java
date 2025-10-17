package com.meli.ordermanager.exception;

/**
 * Custom exception thrown when attempting to modify an order that cannot be changed.
 * 
 * This exception is used when business rules prevent modification of an order
 * due to its current status or other business constraints. For example, orders
 * that are already shipped, delivered, or cancelled typically cannot be modified.
 * 
 * The exception extends RuntimeException to provide unchecked exception behavior,
 * making it easier to handle in service layers without cluttering method signatures
 * with checked exception declarations.
 * 
 * Common scenarios include:
 * - Attempting to update a delivered order
 * - Trying to cancel a shipped order
 * - Modifying order details after payment processing
 * - Status transition violations
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
public class OrderNotModifiableException extends RuntimeException {

    /**
     * Constructs a new OrderNotModifiableException with the specified detail message.
     * 
     * This constructor should be used when providing a specific error message
     * that describes why the order cannot be modified and what the current restrictions are.
     * 
     * @param message the detail message explaining why the order cannot be modified
     */
    public OrderNotModifiableException(String message) {
        super(message);
    }

    /**
     * Constructs a new OrderNotModifiableException with the specified detail message and cause.
     * 
     * This constructor is used when the modification failure is caused by another
     * underlying exception, such as database constraints or external service failures.
     * It allows for proper exception chaining and root cause analysis.
     * 
     * @param message the detail message explaining why the order cannot be modified
     * @param cause   the underlying cause of the exception (Throwable)
     */
    public OrderNotModifiableException(String message, Throwable cause) {
        super(message, cause);
    }
}