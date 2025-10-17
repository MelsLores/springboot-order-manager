package com.meli.ordermanager.repository;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OrderRepository}.
 * Tests repository layer functionality including CRUD operations and custom queries.
 *
 * @author Melany Rivera
 * @since October 16, 2025
 */
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    /**
     * Sets up test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        // Clean the database before each test
        entityManager.clear();
        
        testOrder = new Order();
        testOrder.setCustomerName("John Doe");
        testOrder.setCustomerEmail("john.doe@example.com");
        testOrder.setProductName("Test Product");
        testOrder.setQuantity(2);
        testOrder.setUnitPrice(new BigDecimal("99.99"));
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setShippingAddress("123 Test St, Test City, TC 12345");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Tests saving a new order to the database.
     */
    @Test
    void shouldSaveOrder() {
        // When
        Order savedOrder = orderRepository.save(testOrder);

        // Then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    /**
     * Tests finding an order by its ID.
     */
    @Test
    void shouldFindOrderById() {
        // Given
        Order savedOrder = entityManager.persistAndFlush(testOrder);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getCustomerName()).isEqualTo("John Doe");
    }

    /**
     * Tests finding orders by status.
     */
    @Test
    void shouldFindOrdersByStatus() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        entityManager.persistAndFlush(testOrder);

        Order secondOrder = new Order();
        secondOrder.setCustomerName("Jane Smith");
        secondOrder.setCustomerEmail("jane.smith@example.com");
        secondOrder.setProductName("Another Product");
        secondOrder.setQuantity(1);
        secondOrder.setUnitPrice(new BigDecimal("50.00"));
        secondOrder.setTotalAmount(new BigDecimal("50.00"));
        secondOrder.setStatus(OrderStatus.DELIVERED);
        secondOrder.setShippingAddress("456 Another St, Another City, AC 67890");
        secondOrder.setCreatedAt(LocalDateTime.now());
        secondOrder.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(secondOrder);

        // When
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        // Then
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(pendingOrders.get(0).getCustomerName()).isEqualTo("John Doe");
    }

    /**
     * Tests finding all orders.
     */
    @Test
    void shouldFindAllOrders() {
        // Given
        entityManager.persistAndFlush(testOrder);

        // When
        List<Order> allOrders = orderRepository.findAll();

        // Then
        assertThat(allOrders).isNotEmpty();
    }

    /**
     * Tests updating an existing order.
     */
    @Test
    void shouldUpdateOrder() {
        // Given
        Order savedOrder = entityManager.persistAndFlush(testOrder);
        
        // When
        savedOrder.setStatus(OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    /**
     * Tests deleting an order.
     */
    @Test
    void shouldDeleteOrder() {
        // Given
        Order savedOrder = entityManager.persistAndFlush(testOrder);
        Long orderId = savedOrder.getId();

        // When
        orderRepository.delete(savedOrder);
        Optional<Order> deletedOrder = orderRepository.findById(orderId);

        // Then
        assertThat(deletedOrder).isEmpty();
    }

    /**
     * Tests finding orders by cancelled status returns empty list when no cancelled orders exist.
     */
    @Test
    void shouldReturnEmptyListForCancelledStatus() {
        // Given - testOrder has PENDING status
        entityManager.persistAndFlush(testOrder);

        // When
        List<Order> orders = orderRepository.findByStatus(OrderStatus.CANCELLED);

        // Then
        assertThat(orders).isEmpty();
    }
}