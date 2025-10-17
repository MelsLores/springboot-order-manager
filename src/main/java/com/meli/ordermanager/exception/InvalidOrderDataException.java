package com.meli.ordermanager.exception;

/**
 * Custom exception thrown when order data validation fails.
 * 
 * This exception is used specifically for business logic validation errors
 * that occur when order data doesn't meet the application's business requirements.
 * It extends RuntimeException to provide unchecked exception behavior, making
 * it easier to handle validation errors in service and controller layers.
 * 
 * Examples of usage include:
 * - Invalid quantity values (negative or zero)
 * - Invalid price amounts
 * - Inconsistent order data relationships
 * - Business rule violations
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
public class InvalidOrderDataException extends RuntimeException {

    /**
     * Constructs a new InvalidOrderDataException with the specified detail message.
     * 
     * This constructor should be used when providing a specific error message
     * that describes what validation rule was violated or what data was invalid.
     * 
     * @param message the detail message explaining the validation failure
     */
    public InvalidOrderDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOrderDataException with the specified detail message and cause.
     * 
     * This constructor is useful when the validation failure is caused by another
     * underlying exception, such as number format exceptions or constraint violations.
     * It allows for proper exception chaining and root cause analysis.
     * 
     * @param message the detail message explaining the validation failure
     * @param cause   the underlying cause of the exception (Throwable)
     */
    public InvalidOrderDataException(String message, Throwable cause) {
        super(message, cause);
    }
}