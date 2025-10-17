package com.meli.ordermanager.exception;

/**
 * Custom exception thrown when an invalid order status transition is attempted.
 * 
 * This exception enforces business rules around order status transitions by preventing
 * invalid state changes. For example, an order cannot go from DELIVERED back to PENDING,
 * or from CANCELLED to SHIPPED. The exception helps maintain data integrity and
 * business logic consistency throughout the order lifecycle.
 * 
 * The exception extends RuntimeException to provide unchecked exception behavior,
 * allowing for cleaner service method signatures while still providing detailed
 * error information for debugging and user feedback.
 * 
 * Typical usage scenarios:
 * - Attempting to move a DELIVERED order to PROCESSING
 * - Trying to SHIP a CANCELLED order
 * - Invalid backwards transitions in the order lifecycle
 * - Status updates that violate business workflow rules
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
public class InvalidOrderStatusException extends RuntimeException {

    /**
     * Constructs a new InvalidOrderStatusException with the specified detail message.
     * 
     * This constructor should be used when providing a specific error message
     * that describes the invalid status transition attempt, including current
     * and target statuses for clear error reporting.
     * 
     * @param message the detail message explaining the invalid status transition
     */
    public InvalidOrderStatusException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOrderStatusException with the specified detail message and cause.
     * 
     * This constructor is used when the status validation failure is caused by another
     * underlying exception, such as database constraints or external validation failures.
     * It allows for proper exception chaining and comprehensive error analysis.
     * 
     * @param message the detail message explaining the invalid status transition
     * @param cause   the underlying cause of the exception (Throwable)
     */
    public InvalidOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}