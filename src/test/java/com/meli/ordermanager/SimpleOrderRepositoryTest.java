package com.meli.ordermanager;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Tests saving a new order to the database.
     */
    @Test
    void shouldSaveOrder() {
        // Given
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john.doe@example.com");
        order.setProductName("Test Product");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("99.99"));
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("123 Test St, Test City, TC 12345");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    /**
     * Tests finding orders by status.
     */
    @Test
    void shouldFindOrdersByStatus() {
        // Given
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john.doe@example.com");
        order.setProductName("Test Product");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("99.99"));
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("123 Test St, Test City, TC 12345");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // When
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        // Then
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(pendingOrders.get(0).getCustomerName()).isEqualTo("John Doe");
    }

    /**
     * Tests finding an order by its ID.
     */
    @Test
    void shouldFindOrderById() {
        // Given
        Order order = new Order();
        order.setCustomerName("Jane Smith");
        order.setCustomerEmail("jane.smith@example.com");
        order.setProductName("Another Product");
        order.setQuantity(1);
        order.setUnitPrice(new BigDecimal("50.00"));
        order.setTotalAmount(new BigDecimal("50.00"));
        order.setStatus(OrderStatus.DELIVERED);
        order.setShippingAddress("456 Another St, Another City, AC 67890");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getCustomerName()).isEqualTo("Jane Smith");
    }

    /**
     * Tests finding orders by cancelled status returns empty list when no cancelled orders exist.
     */
    @Test
    void shouldReturnEmptyListForCancelledStatus() {
        // Given
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setCustomerEmail("john.doe@example.com");
        order.setProductName("Test Product");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("99.99"));
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("123 Test St, Test City, TC 12345");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // When
        List<Order> orders = orderRepository.findByStatus(OrderStatus.CANCELLED);

        // Then
        assertThat(orders).isEmpty();
    }
}