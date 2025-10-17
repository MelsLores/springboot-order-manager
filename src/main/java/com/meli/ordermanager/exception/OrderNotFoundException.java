package com.meli.ordermanager.exception;

/**
 * Custom runtime exception thrown when a requested order cannot be found in the system.
 * 
 * This exception is specifically designed for order-related operations when an order
 * with a given identifier does not exist in the database, has been deleted, or is
 * not accessible due to business rules. The exception extends RuntimeException to
 * provide unchecked exception behavior, allowing for cleaner method signatures while
 * still enabling proper error handling and HTTP status mapping in the REST layer.
 * 
 * Usage scenarios:
 * - Order lookup by ID returns no results
 * - Attempting to update a non-existent order
 * - Accessing orders that have been soft-deleted
 * - Authorization failures for restricted orders
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Constructs a new OrderNotFoundException with the specified detail message.
     * 
     * This constructor is typically used when providing a specific error message
     * about the missing order, such as "Order with ID 123 not found".
     * 
     * @param message the detail message explaining the cause of the exception
     * @author Melany Rivera
     * @since October 16, 2025
     */
    public OrderNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new OrderNotFoundException with the specified detail message and cause.
     * 
     * This constructor is used when the exception is caused by another underlying
     * exception, such as database connectivity issues or constraint violations.
     * It allows for proper exception chaining and root cause analysis.
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause   the underlying cause of the exception (Throwable)
     * @author Melany Rivera
     * @since October 16, 2025
     */
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}