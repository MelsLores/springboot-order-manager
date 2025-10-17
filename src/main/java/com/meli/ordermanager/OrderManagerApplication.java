package com.meli.ordermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the MELI Order Management System.
 * 
 * This class serves as the entry point for the Spring Boot application,
 * providing comprehensive order processing capabilities for the e-commerce platform.
 * The application includes RESTful API endpoints, database integration, and
 * automated business logic for managing customer orders efficiently.
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
@SpringBootApplication
public class OrderManagerApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * Initializes the Spring Boot context, configures auto-configurations,
     * and starts the embedded Tomcat server on the configured port.
     * The application will be available at http://localhost:8080/api/v1
     * 
     * @param args command line arguments passed to the application
     * @author Melany Rivera
     * @since October 16, 2025
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderManagerApplication.class, args);
    }
}