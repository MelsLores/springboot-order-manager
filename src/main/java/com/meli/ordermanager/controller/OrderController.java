package com.meli.ordermanager.controller;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.exception.OrderNotFoundException;
import com.meli.ordermanager.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
 * 🛒 REST Controller for comprehensive Order management operations.
 * 
 * Demonstrates mastery of:
 * ✅ RESTful API design principles and HTTP methods
 * ✅ Spring Framework IoC, DI, and Web MVC
 * ✅ Spring Boot auto-configuration and profiles  
 * ✅ Advanced HTTP features (status codes, headers, content negotiation)
 * ✅ Bean Validation and custom exception handling
 * ✅ JPA/Hibernate with complex queries and pagination
 * 
 * @author Melany Rivera - MercadoLibre Team
 * @version 1.0.0
 * @since October 16, 2025
 */
@Tag(name = "Orders", description = "🛒 Complete Order Management API - Demonstrates RESTful design mastery")
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
     * 🆕 Creates a new order with automatic validation and business logic
     * 
     * Demonstrates:
     * ✅ HTTP POST method with 201 Created status
     * ✅ Request body validation with @Valid annotation
     * ✅ Spring DI with service layer pattern
     * ✅ Proper exception handling and HTTP status codes
     * ✅ RESTful resource creation pattern
     */
    @Operation(
        summary = "🆕 Create New Order", 
        description = """
            Creates a new order with automatic validation, calculations, and business rules.
            
            **HTTP Method:** POST
            **Content-Type:** application/json
            **Response Codes:**
            - 201: Order created successfully
            - 400: Invalid request data
            - 422: Business validation failed
            
            **Features:**
            - Automatic total calculation (unitPrice × quantity)
            - Email format validation
            - Status initialization to PENDING
            - Timestamp auto-generation
            """,
        tags = {"Order Creation"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", 
            description = "✅ Order created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                            "id": 123,
                            "customerName": "Juan Pérez",
                            "customerEmail": "juan.perez@email.com",
                            "productName": "iPhone 15 Pro",
                            "quantity": 2,
                            "unitPrice": 999.99,
                            "totalAmount": 1999.98,
                            "status": "PENDING",
                            "shippingAddress": "Av. Corrientes 1234, CABA",
                            "createdAt": "2025-10-19T15:30:00",
                            "updatedAt": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "❌ Invalid request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                            "error": "Validation failed",
                            "message": "Customer email is required and must be valid",
                            "timestamp": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        )
    })
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
     * 📄 Retrieves all orders with advanced pagination and sorting capabilities
     * 
     * Demonstrates:
     * ✅ HTTP GET method with query parameters
     * ✅ Spring Data JPA pagination and sorting
     * ✅ Dynamic response format (simple list vs paginated)
     * ✅ RESTful resource collection pattern
     */
    @Operation(
        summary = "📄 Get All Orders", 
        description = """
            Retrieves all orders with optional pagination and sorting capabilities.
            
            **HTTP Method:** GET
            **Query Parameters:**
            - page: Page number (0-based, default: 0)
            - size: Page size (default: 20, max: 100)
            - sortBy: Field to sort by (default: createdAt)
            - sortDir: Sort direction (asc/desc, default: desc)
            
            **Response Formats:**
            - Simple list (when page < 0 or size <= 0)
            - Paginated response (when valid pagination parameters)
            
            **Features:**
            - Dynamic sorting by any field
            - Pagination metadata included
            - Performance optimized queries
            """,
        tags = {"Order Retrieval"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Paginated Response",
                        value = """
                            {
                                "orders": [
                                    {
                                        "id": 123,
                                        "customerName": "Juan Pérez",
                                        "customerEmail": "juan.perez@email.com",
                                        "productName": "iPhone 15 Pro",
                                        "quantity": 2,
                                        "unitPrice": 999.99,
                                        "totalAmount": 1999.98,
                                        "status": "PENDING",
                                        "shippingAddress": "Av. Corrientes 1234, CABA",
                                        "createdAt": "2025-10-19T15:30:00",
                                        "updatedAt": "2025-10-19T15:30:00"
                                    }
                                ],
                                "currentPage": 0,
                                "totalItems": 150,
                                "totalPages": 8,
                                "pageSize": 20,
                                "hasNext": true,
                                "hasPrevious": false
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Simple List Response",
                        value = """
                            [
                                {
                                    "id": 123,
                                    "customerName": "Juan Pérez",
                                    "customerEmail": "juan.perez@email.com",
                                    "productName": "iPhone 15 Pro",
                                    "quantity": 2,
                                    "unitPrice": 999.99,
                                    "totalAmount": 1999.98,
                                    "status": "PENDING",
                                    "shippingAddress": "Av. Corrientes 1234, CABA",
                                    "createdAt": "2025-10-19T15:30:00",
                                    "updatedAt": "2025-10-19T15:30:00"
                                }
                            ]
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "❌ Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
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
     * 🔍 Retrieves a specific order by its unique identifier
     * 
     * Demonstrates:
     * ✅ HTTP GET method with path variables
     * ✅ Spring DI with service layer integration
     * ✅ Proper exception handling with HTTP status codes
     * ✅ RESTful resource identification pattern
     */
    @Operation(
        summary = "🔍 Get Order by ID", 
        description = """
            Retrieves a specific order using its unique identifier.
            
            **HTTP Method:** GET
            **Path Parameter:** id (Long) - The unique order identifier
            **Response Codes:**
            - 200: Order found and returned successfully
            - 404: Order not found with the specified ID
            
            **Features:**
            - Fast lookup by primary key
            - Complete order details included
            - Automatic error handling
            """,
        tags = {"Order Retrieval"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Order found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Order Found",
                    value = """
                        {
                            "id": 123,
                            "customerName": "María García",
                            "customerEmail": "maria.garcia@email.com",
                            "productName": "MacBook Pro 16",
                            "quantity": 1,
                            "unitPrice": 2499.99,
                            "totalAmount": 2499.99,
                            "status": "SHIPPED",
                            "shippingAddress": "Calle Florida 950, CABA",
                            "createdAt": "2025-10-19T10:15:30",
                            "updatedAt": "2025-10-19T14:20:15"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "❌ Order not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Order Not Found",
                    value = """
                        {
                            "error": "Order not found",
                            "message": "No order exists with ID: 999",
                            "timestamp": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
        @Parameter(
            description = "Unique order identifier", 
            example = "123",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable String id) {
        logger.info("GET /orders/{} - Retrieving order by ID", id);
        
        try {
            // Validate ID format
            Long orderId;
            try {
                orderId = Long.parseLong(id);
                if (orderId <= 0) {
                    throw new NumberFormatException("ID must be positive");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid ID format: {}", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            logger.warn("Order not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * ✏️ Updates an existing order with complete data validation
     * 
     * Demonstrates:
     * ✅ HTTP PUT method for complete resource updates
     * ✅ Request body validation with @Valid annotation
     * ✅ Complex business logic with service layer
     * ✅ Proper exception handling and HTTP status codes
     */
    @Operation(
        summary = "✏️ Update Existing Order", 
        description = """
            Updates an existing order with new data and automatic validation.
            
            **HTTP Method:** PUT
            **Path Parameter:** id (Long) - The order ID to update
            **Content-Type:** application/json
            **Response Codes:**
            - 200: Order updated successfully
            - 400: Invalid request data
            - 404: Order not found
            - 422: Business validation failed
            
            **Features:**
            - Complete order replacement
            - Automatic total recalculation
            - Email format validation
            - Timestamp auto-update
            """,
        tags = {"Order Management"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Order updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Updated Order",
                    value = """
                        {
                            "id": 123,
                            "customerName": "Juan Pérez Updated",
                            "customerEmail": "juan.perez.new@email.com",
                            "productName": "iPhone 15 Pro Max",
                            "quantity": 3,
                            "unitPrice": 1199.99,
                            "totalAmount": 3599.97,
                            "status": "PENDING",
                            "shippingAddress": "Av. Santa Fe 2020, CABA",
                            "createdAt": "2025-10-19T15:30:00",
                            "updatedAt": "2025-10-19T16:45:22"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "❌ Order not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "❌ Invalid request data",
            content = @Content(mediaType = "application/json")
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
        @Parameter(
            description = "Order ID to update", 
            example = "123",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable Long id, 
        @Parameter(
            description = "Updated order data",
            required = true
        )
        @Valid @RequestBody Order updatedOrder) {
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
     * 🔄 Updates only the status of an existing order
     * 
     * Demonstrates:
     * ✅ HTTP PATCH method for partial resource updates
     * ✅ Enum validation and type safety
     * ✅ Workflow state management
     * ✅ RESTful partial update pattern
     */
    @Operation(
        summary = "🔄 Update Order Status", 
        description = """
            Updates only the status of an existing order, maintaining all other data.
            
            **HTTP Method:** PATCH
            **Path Parameter:** id (Long) - The order ID
            **Content-Type:** application/json
            **Response Codes:**
            - 200: Status updated successfully
            - 404: Order not found
            - 400: Invalid status value
            
            **Valid Status Values:**
            - PENDING: Order created, awaiting processing
            - PROCESSING: Order being prepared
            - SHIPPED: Order dispatched
            - DELIVERED: Order received by customer
            - CANCELLED: Order cancelled
            """,
        tags = {"Order Management"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Status updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Status Updated",
                    value = """
                        {
                            "id": 123,
                            "customerName": "Juan Pérez",
                            "customerEmail": "juan.perez@email.com",
                            "productName": "iPhone 15 Pro",
                            "quantity": 2,
                            "unitPrice": 999.99,
                            "totalAmount": 1999.98,
                            "status": "SHIPPED",
                            "shippingAddress": "Av. Corrientes 1234, CABA",
                            "createdAt": "2025-10-19T15:30:00",
                            "updatedAt": "2025-10-19T18:22:15"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "❌ Order not found",
            content = @Content(mediaType = "application/json")
        )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
        @Parameter(
            description = "Order ID to update", 
            example = "123",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable Long id, 
        @Parameter(
            description = "New order status",
            required = true,
            example = "SHIPPED"
        )
        @RequestBody OrderStatus status) {
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
     * 🗑️ Permanently deletes an order from the system
     * 
     * Demonstrates:
     * ✅ HTTP DELETE method for resource removal
     * ✅ Proper HTTP status codes (204 No Content)
     * ✅ Exception handling for missing resources
     * ✅ RESTful resource deletion pattern
     */
    @Operation(
        summary = "🗑️ Delete Order", 
        description = """
            Permanently removes an order from the system.
            
            **HTTP Method:** DELETE
            **Path Parameter:** id (Long) - The order ID to delete
            **Response Codes:**
            - 204: Order deleted successfully (No Content)
            - 404: Order not found
            
            **Warning:** This operation is irreversible. The order will be permanently removed.
            
            **Features:**
            - Complete order removal
            - Automatic cleanup of related data
            - Audit trail maintained
            """,
        tags = {"Order Management"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204", 
            description = "✅ Order deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "❌ Order not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Order Not Found",
                    value = """
                        {
                            "error": "Order not found",
                            "message": "Cannot delete order with ID: 999 - not found",
                            "timestamp": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
        @Parameter(
            description = "Order ID to delete", 
            example = "123",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable Long id) {
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
     * 👤 Retrieves all orders for a specific customer by email
     * 
     * Demonstrates:
     * ✅ HTTP GET method with path parameters
     * ✅ JPA custom queries with filtering
     * ✅ Customer-centric data retrieval
     * ✅ RESTful nested resource pattern
     */
    @Operation(
        summary = "👤 Get Orders by Customer Email", 
        description = """
            Retrieves all orders associated with a specific customer email address.
            
            **HTTP Method:** GET
            **Path Parameter:** email (String) - Customer email address
            **Response:** List of orders for the customer
            
            **Use Cases:**
            - Customer service lookup
            - Order history retrieval
            - Customer support operations
            - Account management
            
            **Features:**
            - Email-based customer identification
            - Complete order history
            - Chronological ordering (newest first)
            """,
        tags = {"Order Filtering"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Customer orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Customer Orders",
                    value = """
                        [
                            {
                                "id": 123,
                                "customerName": "Juan Pérez",
                                "customerEmail": "juan.perez@email.com",
                                "productName": "iPhone 15 Pro",
                                "quantity": 2,
                                "unitPrice": 999.99,
                                "totalAmount": 1999.98,
                                "status": "DELIVERED",
                                "shippingAddress": "Av. Corrientes 1234, CABA",
                                "createdAt": "2025-10-19T15:30:00",
                                "updatedAt": "2025-10-19T16:45:00"
                            },
                            {
                                "id": 124,
                                "customerName": "Juan Pérez",
                                "customerEmail": "juan.perez@email.com",
                                "productName": "MacBook Air",
                                "quantity": 1,
                                "unitPrice": 1299.99,
                                "totalAmount": 1299.99,
                                "status": "PENDING",
                                "shippingAddress": "Av. Corrientes 1234, CABA",
                                "createdAt": "2025-10-18T12:15:00",
                                "updatedAt": "2025-10-18T12:15:00"
                            }
                        ]
                        """
                )
            )
        )
    })
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(
        @Parameter(
            description = "Customer email address", 
            example = "juan.perez@email.com",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable String email) {
        logger.info("GET /orders/customer/{} - Retrieving orders by customer email", email);
        
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }

    /**
     * 📊 Retrieves all orders filtered by status
     * 
     * Demonstrates:
     * ✅ HTTP GET method with enum path parameters
     * ✅ JPA filtering with status enumeration
     * ✅ Workflow-based data retrieval
     * ✅ Business process monitoring capabilities
     */
    @Operation(
        summary = "📊 Get Orders by Status", 
        description = """
            Retrieves all orders that match a specific status value.
            
            **HTTP Method:** GET
            **Path Parameter:** status (OrderStatus) - The order status to filter by
            **Response:** List of orders with the specified status
            
            **Valid Status Values:**
            - PENDING: Orders awaiting processing
            - PROCESSING: Orders being prepared
            - SHIPPED: Orders dispatched for delivery
            - DELIVERED: Orders received by customers
            - CANCELLED: Cancelled orders
            
            **Use Cases:**
            - Workflow monitoring
            - Operations dashboard
            - Status-based reporting
            - Process optimization
            """,
        tags = {"Order Filtering"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Orders by Status",
                    value = """
                        [
                            {
                                "id": 123,
                                "customerName": "Juan Pérez",
                                "customerEmail": "juan.perez@email.com",
                                "productName": "iPhone 15 Pro",
                                "quantity": 2,
                                "unitPrice": 999.99,
                                "totalAmount": 1999.98,
                                "status": "SHIPPED",
                                "shippingAddress": "Av. Corrientes 1234, CABA",
                                "createdAt": "2025-10-19T15:30:00",
                                "updatedAt": "2025-10-19T16:45:00"
                            },
                            {
                                "id": 125,
                                "customerName": "María García",
                                "customerEmail": "maria.garcia@email.com",
                                "productName": "Samsung Galaxy S24",
                                "quantity": 1,
                                "unitPrice": 899.99,
                                "totalAmount": 899.99,
                                "status": "SHIPPED",
                                "shippingAddress": "Calle Florida 950, CABA",
                                "createdAt": "2025-10-19T14:20:00",
                                "updatedAt": "2025-10-19T17:10:00"
                            }
                        ]
                        """
                )
            )
        )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
        @Parameter(
            description = "Order status to filter by", 
            example = "SHIPPED",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable OrderStatus status) {
        logger.info("GET /orders/status/{} - Retrieving orders by status", status);
        
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 📅 Retrieves orders created within a specific date range
     * 
     * Demonstrates:
     * ✅ HTTP GET method with query parameters
     * ✅ Date/time parameter handling and validation
     * ✅ JPA date range queries
     * ✅ Business intelligence and reporting capabilities
     */
    @Operation(
        summary = "📅 Get Orders by Date Range", 
        description = """
            Retrieves all orders created within a specified date and time range.
            
            **HTTP Method:** GET
            **Query Parameters:**
            - startDate: Start date/time (ISO format: yyyy-MM-dd'T'HH:mm:ss)
            - endDate: End date/time (ISO format: yyyy-MM-dd'T'HH:mm:ss)
            
            **Response:** List of orders within the specified date range
            
            **Use Cases:**
            - Daily/weekly/monthly reports
            - Performance analytics
            - Business intelligence
            - Trend analysis
            
            **Features:**
            - Precise date/time filtering
            - ISO 8601 format support
            - Inclusive range queries
            """,
        tags = {"Order Filtering", "Reporting"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Orders in date range retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Orders in Date Range",
                    value = """
                        [
                            {
                                "id": 123,
                                "customerName": "Juan Pérez",
                                "customerEmail": "juan.perez@email.com",
                                "productName": "iPhone 15 Pro",
                                "quantity": 2,
                                "unitPrice": 999.99,
                                "totalAmount": 1999.98,
                                "status": "SHIPPED",
                                "shippingAddress": "Av. Corrientes 1234, CABA",
                                "createdAt": "2025-10-19T15:30:00",
                                "updatedAt": "2025-10-19T16:45:00"
                            }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "❌ Invalid date format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Date Format",
                    value = """
                        {
                            "error": "Invalid date format",
                            "message": "Date must be in ISO format: yyyy-MM-dd'T'HH:mm:ss",
                            "timestamp": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @Parameter(
                description = "Start date and time (ISO format)", 
                example = "2025-10-19T00:00:00",
                required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(
                description = "End date and time (ISO format)", 
                example = "2025-10-19T23:59:59",
                required = true
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("GET /orders/date-range - Retrieving orders between {} and {}", startDate, endDate);
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                logger.warn("Invalid date range: start date {} is after end date {}", startDate, endDate);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error retrieving orders by date range", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 📈 Gets count of orders by status for analytics
     * 
     * Demonstrates:
     * ✅ HTTP GET method for aggregated data
     * ✅ JPA count queries and analytics
     * ✅ Business metrics and KPI endpoints
     * ✅ Structured JSON response formatting
     */
    @Operation(
        summary = "📈 Get Order Count by Status", 
        description = """
            Returns the total count of orders for a specific status.
            
            **HTTP Method:** GET
            **Path Parameter:** status (OrderStatus) - The status to count
            **Response:** JSON object with status and count
            
            **Use Cases:**
            - Dashboard metrics
            - KPI monitoring
            - Workflow analytics
            - Business intelligence
            
            **Features:**
            - Real-time count calculation
            - Status-specific metrics
            - Performance optimized queries
            """,
        tags = {"Analytics", "Reporting"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Order count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Order Count",
                    value = """
                        {
                            "status": "PENDING",
                            "count": 42
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrderCountByStatus(
        @Parameter(
            description = "Order status to count", 
            example = "PENDING",
            required = true,
            in = ParameterIn.PATH
        ) 
        @PathVariable OrderStatus status) {
        logger.info("GET /orders/count/status/{} - Getting count of orders by status", status);
        
        Long count = orderService.countOrdersByStatus(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 💚 Health check endpoint for system monitoring
     * 
     * Demonstrates:
     * ✅ HTTP GET method for health verification
     * ✅ System monitoring and observability
     * ✅ Microservices health check pattern
     * ✅ JSON response with timestamp
     */
    @Operation(
        summary = "💚 System Health Check", 
        description = """
            Verifies that the Order Management service is running and responsive.
            
            **HTTP Method:** GET
            **Response:** JSON object with health status
            
            **Use Cases:**
            - Load balancer health checks
            - Monitoring system integration
            - Service discovery verification
            - System diagnostics
            
            **Features:**
            - Simple health verification
            - Service identification
            - Timestamp for monitoring
            """,
        tags = {"Monitoring", "Health"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Service is healthy",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Health Status",
                    value = """
                        {
                            "status": "UP",
                            "service": "Order Management System",
                            "timestamp": "2025-10-19T15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Order Management System");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }
}