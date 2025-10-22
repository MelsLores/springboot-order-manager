package com.meli.ordermanager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Global exception handler for the Order Manager application.
 * 
 * This class provides centralized exception handling across the entire application
 * using Spring's @RestControllerAdvice annotation. It ensures consistent error
 * responses and proper HTTP status codes for different types of exceptions.
 * 
 * The handler catches various exception types and converts them into appropriate
 * HTTP responses with structured error information, making it easier for clients
 * to understand and handle errors programmatically.
 * 
 * Features:
 * - Consistent error response format across all endpoints
 * - Proper HTTP status code mapping for different exception types
 * - Detailed error logging for debugging and monitoring
 * - Validation error handling with field-specific details
 * - Security-conscious error messaging
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles OrderNotFoundException and returns 404 Not Found.
     * 
     * @param ex the OrderNotFoundException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 404 status
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFoundException(
            OrderNotFoundException ex, WebRequest request) {
        
        logger.warn("Order not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Order Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidOrderDataException and returns 400 Bad Request.
     * 
     * @param ex the InvalidOrderDataException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(InvalidOrderDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderDataException(
            InvalidOrderDataException ex, WebRequest request) {
        
        logger.warn("Invalid order data: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Order Data",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles OrderNotModifiableException and returns 409 Conflict.
     * 
     * @param ex the OrderNotModifiableException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 409 status
     */
    @ExceptionHandler(OrderNotModifiableException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotModifiableException(
            OrderNotModifiableException ex, WebRequest request) {
        
        logger.warn("Order not modifiable: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Order Not Modifiable",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles InvalidOrderStatusException and returns 422 Unprocessable Entity.
     * 
     * @param ex the InvalidOrderStatusException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 422 status
     */
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderStatusException(
            InvalidOrderStatusException ex, WebRequest request) {
        
        logger.warn("Invalid order status transition: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Invalid Order Status Transition",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles OrderProcessingException and returns 422 Unprocessable Entity.
     * 
     * @param ex the OrderProcessingException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 422 status
     */
    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleOrderProcessingException(
            OrderProcessingException ex, WebRequest request) {
        
        logger.warn("Order processing failed: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Order Processing Failed",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles MethodArgumentTypeMismatchException (e.g., invalid ID format) and returns 400 Bad Request.
     * 
     * @param ex the MethodArgumentTypeMismatchException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Invalid parameter format: {} for parameter {}", ex.getValue(), ex.getName());
        
        String message = String.format("Invalid format for parameter '%s': '%s'", ex.getName(), ex.getValue());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Parameter Format",
            message,
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles DateTimeParseException and returns 400 Bad Request.
     * 
     * @param ex the DateTimeParseException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(
            DateTimeParseException ex, WebRequest request) {
        
        logger.warn("Invalid date format: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Date Format",
            "Invalid date format. Please use ISO format (yyyy-MM-ddTHH:mm:ss)",
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MissingServletRequestParameterException and returns 400 Bad Request.
     * 
     * @param ex the MissingServletRequestParameterException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        
        logger.warn("Missing required parameter: {}", ex.getParameterName());
        
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Missing Required Parameter",
            message,
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException and returns 400 Bad Request.
     * 
     * @param ex the IllegalArgumentException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Request",
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors from @Valid annotations and returns 400 Bad Request.
     * 
     * @param ex the MethodArgumentNotValidException that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with detailed validation error information and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation failed for request: {}", request.getDescription(false));
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Request validation failed. Check the field errors for details.",
            request.getDescription(false)
        );
        
        errorResponse.put("fieldErrors", fieldErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unexpected exceptions and returns 500 Internal Server Error.
     * 
     * @param ex the generic Exception that was thrown
     * @param request the web request that caused the exception
     * @return ResponseEntity with error details and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a standardized error response structure.
     * 
     * @param status the HTTP status code
     * @param error the error type/title
     * @param message the detailed error message
     * @param path the request path that caused the error
     * @return a Map containing the structured error response
     */
    private Map<String, Object> createErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path.replace("uri=", ""));
        return errorResponse;
    }

    /**
     * Handles JSON parse / deserialization errors (e.g., unknown enum values) and returns 400 Bad Request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        logger.warn("Malformed JSON request: {}", ex.getMessage());

        // If the cause is an InvalidFormatException (unknown enum value), extract helpful details
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown";
            String rejected = ife.getValue() != null ? ife.getValue().toString() : "<null>";

            String allowed = "";
            try {
                Class<?> clazz = ife.getTargetType();
                if (clazz.isEnum()) {
                    Object[] constants = clazz.getEnumConstants();
                    allowed = Arrays.stream(constants).map(Object::toString).collect(Collectors.joining(", "));
                }
            } catch (Exception ignore) {
            }

            String message = String.format("Invalid value '%s' for type %s. Allowed values: %s", rejected, targetType, allowed);

            Map<String, Object> errorResponse = createErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Malformed JSON",
                    message,
                    request.getDescription(false)
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON",
                "Request body is not readable or is malformed JSON",
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}