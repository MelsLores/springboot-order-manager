package com.meli.ordermanager.exception;

/**
 * Custom exception thrown when order processing fails due to business logic violations.
 * 
 * This exception represents failures in order processing that are related to business
 * rules rather than technical issues. It's used when the system cannot complete an
 * order operation due to business constraints, workflow violations, or policy restrictions.
 * 
 * The exception extends RuntimeException to provide unchecked exception behavior,
 * making it easier to handle business logic failures without requiring explicit
 * exception handling in every method signature.
 * 
 * Common usage scenarios include:
 * - Insufficient inventory for order fulfillment
 * - Order processing workflow violations
 * - Business policy restrictions (e.g., order limits, geographic restrictions)
 * - Integration failures with external business systems
 * - Payment processing business rule violations
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
public class OrderProcessingException extends RuntimeException {

    /**
     * Constructs a new OrderProcessingException with the specified detail message.
     * 
     * This constructor should be used when providing a specific error message
     * that describes the business logic failure and provides context about
     * what processing step failed and why.
     * 
     * @param message the detail message explaining the order processing failure
     */
    public OrderProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new OrderProcessingException with the specified detail message and cause.
     * 
     * This constructor is used when the processing failure is caused by another
     * underlying exception, such as external service failures, database issues,
     * or integration problems. It allows for proper exception chaining and
     * comprehensive error analysis.
     * 
     * @param message the detail message explaining the order processing failure
     * @param cause   the underlying cause of the exception (Throwable)
     */
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}