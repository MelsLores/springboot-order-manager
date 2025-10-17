package com.meli.ordermanager.service;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.exception.OrderNotFoundException;
import com.meli.ordermanager.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Service class for comprehensive Order business logic management.
 * 
 * This service layer component encapsulates all business logic for order
 * management operations, providing a clean separation between the web layer
 * and data access layer. It implements transaction management, business rule
 * enforcement, and coordinates complex operations across multiple entities.
 * 
 * Key responsibilities:
 * - Order lifecycle management (creation, updates, status transitions)
 * - Business rule validation and enforcement
 * - Transaction boundary management with @Transactional
 * - Complex query operations and data aggregation
 * - Exception handling and error management
 * - Audit logging for business operations
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    /**
     * Constructor for OrderService with dependency injection.
     * 
     * @param orderRepository the repository for order data access
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Creates a new order in the system.
     * 
     * @param order the order to be created
     * @return the saved order with generated ID
     * @throws IllegalArgumentException if the order data is invalid
     */
    public Order createOrder(Order order) {
        validateOrder(order);
        
        logger.info("Creating new order for customer: {}", order.getCustomerEmail());
        
        // Set initial status if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        
        return savedOrder;
    }

    /**
     * Retrieves all orders from the system.
     * 
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        logger.info("Retrieving all orders");
        return orderRepository.findAll();
    }

    /**
     * Retrieves all orders with pagination support.
     * 
     * @param pageable pagination information
     * @return page of orders
     */
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        logger.info("Retrieving orders with pagination: page {}, size {}", 
                   pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAll(pageable);
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param id the order ID
     * @return the order if found
     * @throws OrderNotFoundException if the order is not found
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        logger.info("Retrieving order with ID: {}", id);
        
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Order not found with id: {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
    }

    /**
     * Updates an existing order.
     * 
     * @param id           the ID of the order to update
     * @param updatedOrder the updated order data
     * @return the updated order
     * @throws OrderNotFoundException if the order is not found
     */
    public Order updateOrder(Long id, Order updatedOrder) {
        logger.info("Updating order with ID: {}", id);
        
        Order existingOrder = getOrderById(id);
        
        // Update fields
        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setCustomerEmail(updatedOrder.getCustomerEmail());
        existingOrder.setProductName(updatedOrder.getProductName());
        existingOrder.setQuantity(updatedOrder.getQuantity());
        existingOrder.setUnitPrice(updatedOrder.getUnitPrice());
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setStatus(updatedOrder.getStatus());
        
        validateOrder(existingOrder);
        
        Order savedOrder = orderRepository.save(existingOrder);
        logger.info("Order updated successfully with ID: {}", savedOrder.getId());
        
        return savedOrder;
    }

    /**
     * Updates the status of an existing order.
     * 
     * @param id     the ID of the order to update
     * @param status the new status
     * @return the updated order
     * @throws OrderNotFoundException if the order is not found
     */
    public Order updateOrderStatus(Long id, OrderStatus status) {
        logger.info("Updating order status for ID: {} to {}", id, status);
        
        Order order = getOrderById(id);
        order.setStatus(status);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order status updated successfully for ID: {}", savedOrder.getId());
        
        return savedOrder;
    }

    /**
     * Deletes an order by its ID.
     * 
     * @param id the ID of the order to delete
     * @throws OrderNotFoundException if the order is not found
     */
    public void deleteOrder(Long id) {
        logger.info("Deleting order with ID: {}", id);
        
        if (!orderRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent order with ID: {}", id);
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        
        orderRepository.deleteById(id);
        logger.info("Order deleted successfully with ID: {}", id);
    }

    /**
     * Retrieves orders by customer email.
     * 
     * @param customerEmail the customer email to search for
     * @return list of orders for the customer
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        logger.info("Retrieving orders for customer email: {}", customerEmail);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomerEmail().equalsIgnoreCase(customerEmail))
                .toList();
    }

    /**
     * Retrieves orders by status.
     * 
     * @param status the order status to filter by
     * @return list of orders with the specified status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        logger.info("Retrieving orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }

    /**
     * Retrieves orders created within a date range.
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of orders created within the date range
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving orders between {} and {}", startDate, endDate);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(startDate.minusSeconds(1)) 
                        && order.getCreatedAt().isBefore(endDate.plusSeconds(1)))
                .toList();
    }

    /**
     * Counts the number of orders with a specific status.
     * 
     * @param status the order status to count
     * @return number of orders with the specified status
     */
    @Transactional(readOnly = true)
    public Long countOrdersByStatus(OrderStatus status) {
        logger.info("Counting orders with status: {}", status);
        List<Order> orders = orderRepository.findByStatus(status);
        return (long) orders.size();
    }

    /**
     * Validates order data before saving.
     * 
     * @param order the order to validate
     * @throws IllegalArgumentException if the order data is invalid
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        
        if (order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        
        if (order.getProductName() == null || order.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        if (order.getUnitPrice() == null || order.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than 0");
        }
        
        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
    }
}