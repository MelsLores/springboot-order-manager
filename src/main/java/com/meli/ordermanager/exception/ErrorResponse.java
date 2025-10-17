package com.meli.ordermanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response structure for the Order Manager API.
 * 
 * This class provides a consistent format for all error responses returned by
 * the application. It includes essential information such as timestamp, HTTP status,
 * error type, message, and request path to help clients understand and handle
 * errors appropriately.
 * 
 * The response structure follows REST API best practices and provides enough
 * information for debugging while maintaining security by not exposing sensitive
 * internal details. Optional fields like validation errors are included only
 * when relevant.
 * 
 * Features:
 * - Consistent error response format
 * - ISO 8601 timestamp formatting
 * - Optional field inclusion with @JsonInclude
 * - Support for detailed validation errors
 * - Builder pattern for easy construction
 * 
 * @author Melany Rivera
 * @since October 16, 2025
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    /**
     * Default constructor for JSON deserialization.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with basic error information.
     * 
     * @param status the HTTP status code
     * @param error the error type or title
     * @param message the detailed error message
     * @param path the request path that caused the error
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Constructor with validation error details.
     * 
     * @param status the HTTP status code
     * @param error the error type or title
     * @param message the detailed error message
     * @param path the request path that caused the error
     * @param fieldErrors map of field names to their error messages
     */
    public ErrorResponse(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        this(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

    /**
     * Creates a new ErrorResponse builder.
     * 
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    /**
     * Builder class for constructing ErrorResponse instances.
     * 
     * Provides a fluent interface for building error responses with optional fields.
     */
    public static class Builder {
        private final ErrorResponse errorResponse = new ErrorResponse();

        public Builder status(int status) {
            errorResponse.setStatus(status);
            return this;
        }

        public Builder error(String error) {
            errorResponse.setError(error);
            return this;
        }

        public Builder message(String message) {
            errorResponse.setMessage(message);
            return this;
        }

        public Builder path(String path) {
            errorResponse.setPath(path);
            return this;
        }

        public Builder fieldErrors(Map<String, String> fieldErrors) {
            errorResponse.setFieldErrors(fieldErrors);
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            errorResponse.setTimestamp(timestamp);
            return this;
        }

        public ErrorResponse build() {
            return errorResponse;
        }
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", fieldErrors=" + fieldErrors +
                '}';
    }
}