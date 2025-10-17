package com.meli.ordermanager.service;

import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.exception.OrderNotFoundException;
import com.meli.ordermanager.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OrderService}.
 * Tests business logic layer functionality including CRUD operations,
 * pagination, and exception handling.
 *
 * @author Melany Rivera
 * @since October 16, 2025
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Order testOrderForCreation;

    /**
     * Sets up test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
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

        testOrderForCreation = new Order();
        testOrderForCreation.setCustomerName("John Doe");
        testOrderForCreation.setCustomerEmail("john.doe@example.com");
        testOrderForCreation.setProductName("Test Product");
        testOrderForCreation.setQuantity(2);
        testOrderForCreation.setUnitPrice(new BigDecimal("99.99"));
        testOrderForCreation.setTotalAmount(new BigDecimal("199.98"));
        testOrderForCreation.setShippingAddress("123 Test St, Test City, TC 12345");
    }

    /**
     * Tests creating a new order successfully.
     */
    @Test
    void shouldCreateOrderSuccessfully() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order createdOrder = orderService.createOrder(testOrderForCreation);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(createdOrder.getTotalAmount()).isEqualTo(new BigDecimal("199.98"));
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Tests retrieving an order by ID successfully.
     */
    @Test
    void shouldGetOrderByIdSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order foundOrder = orderService.getOrderById(1L);

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(1L);
        assertThat(foundOrder.getCustomerName()).isEqualTo("John Doe");
        verify(orderRepository).findById(1L);
    }

    /**
     * Tests retrieving a non-existent order throws exception.
     */
    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found with id: 999");
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests retrieving all orders with pagination.
     */
    @Test
    void shouldGetAllOrdersWithPagination() {
        // Given
        List<Order> orders = List.of(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), 1);
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // When
        Page<Order> result = orderService.getAllOrders(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        verify(orderRepository).findAll(any(Pageable.class));
    }

    /**
     * Tests retrieving orders by status.
     */
    @Test
    void shouldGetOrdersByStatus() {
        // Given
        List<Order> pendingOrders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        // When
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    /**
     * Tests updating an order successfully.
     */
    @Test
    void shouldUpdateOrderSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order updateRequest = new Order();
        updateRequest.setCustomerName("Jane Doe");
        updateRequest.setCustomerEmail("jane.doe@example.com");
        updateRequest.setProductName("Updated Product");
        updateRequest.setQuantity(3);
        updateRequest.setUnitPrice(new BigDecimal("149.99"));
        updateRequest.setTotalAmount(new BigDecimal("449.97"));
        updateRequest.setShippingAddress("456 Updated St, Updated City, UC 67890");

        // When
        Order updatedOrder = orderService.updateOrder(1L, updateRequest);

        // Then
        assertThat(updatedOrder).isNotNull();
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Tests updating order status successfully.
     */
    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Tests deleting an order successfully.
     */
    @Test
    void shouldDeleteOrderSuccessfully() {
        // Given
        when(orderRepository.existsById(1L)).thenReturn(true);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository).existsById(1L);
        verify(orderRepository).deleteById(1L);
    }

    /**
     * Tests deleting a non-existent order throws exception.
     */
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentOrder() {
        // Given
        when(orderRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found with id: 999");
        verify(orderRepository).existsById(999L);
    }

    /**
     * Tests calculating total amount correctly.
     */
    @Test
    void shouldCalculateTotalAmountCorrectly() {
        // Given
        testOrderForCreation.setQuantity(5);
        testOrderForCreation.setUnitPrice(new BigDecimal("29.99"));
        testOrderForCreation.setTotalAmount(new BigDecimal("149.95"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order createdOrder = orderService.createOrder(testOrderForCreation);

        // Then
        assertThat(createdOrder.getTotalAmount()).isEqualTo(new BigDecimal("149.95"));
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Tests retrieving orders with empty result.
     */
    @Test
    void shouldReturnEmptyListWhenNoOrdersFound() {
        // Given
        when(orderRepository.findByStatus(OrderStatus.CANCELLED)).thenReturn(List.of());

        // When
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.CANCELLED);

        // Then
        assertThat(result).isEmpty();
        verify(orderRepository).findByStatus(OrderStatus.CANCELLED);
    }
}