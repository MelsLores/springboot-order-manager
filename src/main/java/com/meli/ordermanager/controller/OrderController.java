package com.meli.ordermanager.controller;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.exception.OrderNotFoundException;
import com.meli.ordermanager.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for comprehensive Order management operations.
 * 
 * This controller provides a complete RESTful API for order management,
 * implementing all CRUD operations plus advanced features such as search,
 * filtering, pagination, and system health monitoring. It handles HTTP
 * requests with proper validation, error handling, and returns appropriate
 * responses following REST best practices.
 * 
 * Supported operations:
 * - Create new orders with automatic validation and calculations
 * - Retrieve orders by ID, customer email, status, or date range
 * - Update complete order information or just status
 * - Delete orders with proper verification
 * - Paginated listing with sorting capabilities
 * - Statistical information and order counting
 * - System health and monitoring endpoints
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    /**
     * Constructor for OrderController with dependency injection.
     * 
     * Initializes the controller with the required OrderService dependency
     * using constructor-based dependency injection, following Spring Boot
     * best practices for immutable dependencies.
     * 
     * @param orderService the service layer component for order business logic
     * @author Melany Rivera
     * @since October 16, 2025
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order.
     * 
     * @param order the order data to create
     * @return ResponseEntity with the created order and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        logger.info("POST /orders - Creating new order for customer: {}", order.getCustomerEmail());
        
        try {
            Order createdOrder = orderService.createOrder(order);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all orders with optional pagination.
     * 
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @param sortBy the field to sort by (default: createdAt)
     * @param sortDir the sort direction (default: desc)
     * @return ResponseEntity with list of orders or paginated results
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("GET /orders - Retrieving orders (page: {}, size: {}, sortBy: {}, sortDir: {})", 
                   page, size, sortBy, sortDir);
        
        try {
            if (page >= 0 && size > 0) {
                // Paginated request
                Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                           Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);
                
                Page<Order> ordersPage = orderService.getAllOrders(pageable);
                
                Map<String, Object> response = new HashMap<>();
                response.put("orders", ordersPage.getContent());
                response.put("currentPage", ordersPage.getNumber());
                response.put("totalItems", ordersPage.getTotalElements());
                response.put("totalPages", ordersPage.getTotalPages());
                response.put("pageSize", ordersPage.getSize());
                response.put("hasNext", ordersPage.hasNext());
                response.put("hasPrevious", ordersPage.hasPrevious());
                
                return ResponseEntity.ok(response);
            } else {
                // Non-paginated request
                List<Order> orders = orderService.getAllOrders();
                return ResponseEntity.ok(orders);
            }
        } catch (Exception e) {
            logger.error("Error retrieving orders: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a specific order by its ID.
     * 
     * @param id the order ID
     * @return ResponseEntity with the order or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("GET /orders/{} - Retrieving order by ID", id);
        
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing order.
     * 
     * @param id           the ID of the order to update
     * @param updatedOrder the updated order data
     * @return ResponseEntity with the updated order
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order updatedOrder) {
        logger.info("PUT /orders/{} - Updating order", id);
        
        try {
            Order order = orderService.updateOrder(id, updatedOrder);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for update with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order data for update: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates the status of an existing order.
     * 
     * @param id     the ID of the order to update
     * @param status the new status
     * @return ResponseEntity with the updated order
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        logger.info("PATCH /orders/{}/status - Updating order status to {}", id, status);
        
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for status update with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an order by its ID.
     * 
     * @param id the ID of the order to delete
     * @return ResponseEntity with 204 status or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        logger.info("DELETE /orders/{} - Deleting order", id);
        
        try {
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found for deletion with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves orders by customer email.
     * 
     * @param email the customer email to search for
     * @return ResponseEntity with list of orders for the customer
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(@PathVariable String email) {
        logger.info("GET /orders/customer/{} - Retrieving orders by customer email", email);
        
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves orders by status.
     * 
     * @param status the order status to filter by
     * @return ResponseEntity with list of orders with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.info("GET /orders/status/{} - Retrieving orders by status", status);
        
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves orders created within a date range.
     * 
     * @param startDate the start date (format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate   the end date (format: yyyy-MM-dd'T'HH:mm:ss)
     * @return ResponseEntity with list of orders within the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("GET /orders/date-range - Retrieving orders between {} and {}", startDate, endDate);
        
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    /**
     * Gets count of orders by status.
     * 
     * @param status the status to count
     * @return ResponseEntity with count of orders
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrderCountByStatus(@PathVariable OrderStatus status) {
        logger.info("GET /orders/count/status/{} - Getting count of orders by status", status);
        
        Long count = orderService.countOrdersByStatus(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Order Management System");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }
}