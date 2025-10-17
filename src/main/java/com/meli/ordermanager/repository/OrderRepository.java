package com.meli.ordermanager.repository;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity data access operations.
 * 
 * This Spring Data JPA repository provides comprehensive data access methods for
 * the Order entity, including standard CRUD operations inherited from JpaRepository
 * and custom query methods for business-specific data retrieval needs.
 * 
 * Key Features:
 * - Full CRUD operations (Create, Read, Update, Delete)
 * - Pagination and sorting capabilities
 * - Custom query methods for business logic
 * - Automatic transaction management
 * - Spring Data JPA query derivation
 * 
 * The repository follows Spring Data naming conventions for automatic query
 * generation and provides type-safe database operations with compile-time validation.
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds all orders with the specified status.
     * 
     * This method uses Spring Data JPA query derivation to automatically generate
     * the appropriate SQL query based on the method name. It enables filtering
     * orders by their current status for business reporting and workflow management.
     * 
     * @param status the order status to filter by (must not be null)
     * @return list of orders matching the specified status (never null, may be empty)
     * @throws IllegalArgumentException if status is null
     */
    List<Order> findByStatus(OrderStatus status);
}