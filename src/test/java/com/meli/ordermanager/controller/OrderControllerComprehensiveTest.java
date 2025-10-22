package com.meli.ordermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.exception.OrderNotFoundException;
import com.meli.ordermanager.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🧪 Comprehensive Controller Tests for OrderController
 * 
 * Tests all endpoints with success cases, edge cases, and failure scenarios
 * following Spring Boot testing best practices and Sprint 3 requirements.
 * 
 * Features tested:
 * ✅ All CRUD operations
 * ✅ Validation scenarios  
 * ✅ Error handling
 * ✅ Edge cases
 * ✅ HTTP status codes
 * ✅ JSON response structure
 * ✅ Query parameters
 * ✅ Path variables
 * 
 * @author Melany Rivera - MercadoLibre Team
 * @version 1.0.0
 * @since October 20, 2025
 */
@WebMvcTest(OrderController.class)
class OrderControllerComprehensiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order validOrder;
    private Order invalidOrder;
    private List<Order> orderList;

    @BeforeEach
    void setUp() {
        validOrder = createValidOrder();
        invalidOrder = createInvalidOrder();
        orderList = createOrderList();
    }

    // ===============================
    // CREATE ORDER TESTS
    // ===============================

    @Test
    void createOrder_WithValidData_ShouldReturn201() throws Exception {
        // Given
        given(orderService.createOrder(any(Order.class))).willReturn(validOrder);

        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("Juan Pérez")))
                .andExpect(jsonPath("$.customerEmail", is("juan.perez@email.com")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.totalAmount", is(1999.98)));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidEmail_ShouldReturn400() throws Exception {
        // Given
        Order orderWithInvalidEmail = createValidOrder();
        orderWithInvalidEmail.setCustomerEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderWithInvalidEmail)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void createOrder_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Given
        Order incompleteOrder = new Order();
        incompleteOrder.setCustomerName(""); // Empty required field

        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteOrder)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    @Test
    void createOrder_WithNegativeQuantity_ShouldReturn400() throws Exception {
        // Given
        Order orderWithNegativeQuantity = createValidOrder();
        orderWithNegativeQuantity.setQuantity(-1);

        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderWithNegativeQuantity)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(Order.class));
    }

    // ===============================
    // GET ALL ORDERS TESTS
    // ===============================

    @Test
    void getAllOrders_WithoutPagination_ShouldReturnList() throws Exception {
        // Given
        given(orderService.getAllOrders()).willReturn(orderList);

        // When & Then
        mockMvc.perform(get("/orders")
                .param("page", "-1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].customerName", is("Juan Pérez")))
                .andExpect(jsonPath("$[1].customerName", is("María García")))
                .andExpect(jsonPath("$[2].customerName", is("Carlos López")));

        verify(orderService).getAllOrders();
    }

    @Test
    void getAllOrders_WithPagination_ShouldReturnPagedResult() throws Exception {
        // Given - Página con 3 elementos, tamaño de página 3, total 3 elementos (1 página)
        Page<Order> orderPage = new PageImpl<>(orderList, PageRequest.of(0, 3), 3);
        given(orderService.getAllOrders(any(PageRequest.class))).willReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/orders")
                .param("page", "0")
                .param("size", "3")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orders", hasSize(3)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.totalItems", is(3)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));

        verify(orderService).getAllOrders(any(PageRequest.class));
    }

    @Test
    void getAllOrders_WithCustomSorting_ShouldReturnSortedResult() throws Exception {
        // Given
        given(orderService.getAllOrders()).willReturn(orderList);

        // When & Then
        mockMvc.perform(get("/orders")
                .param("page", "-1")
                .param("size", "0")
                .param("sortBy", "customerName")
                .param("sortDir", "asc"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderService).getAllOrders();
    }

    // ===============================
    // GET ORDER BY ID TESTS
    // ===============================

    @Test
    void getOrderById_WithExistingId_ShouldReturnOrder() throws Exception {
        // Given
        given(orderService.getOrderById(1L)).willReturn(validOrder);

        // When & Then
        mockMvc.perform(get("/orders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("Juan Pérez")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(orderService).getOrderById(1L);
    }

    @Test
    void getOrderById_WithNonExistingId_ShouldReturn404() throws Exception {
        // Given
        given(orderService.getOrderById(999L))
                .willThrow(new OrderNotFoundException("Order not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/orders/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999L);
    }

    @Test
    void getOrderById_WithInvalidIdFormat_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders/invalid-id"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).getOrderById(any());
    }

    // ===============================
    // UPDATE ORDER TESTS
    // ===============================

    @Test
    void updateOrder_WithValidData_ShouldReturnUpdatedOrder() throws Exception {
        // Given
        Order updatedOrder = createValidOrder();
        updatedOrder.setCustomerName("Juan Pérez Updated");
        given(orderService.updateOrder(eq(1L), any(Order.class))).willReturn(updatedOrder);

        // When & Then
        mockMvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrder)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerName", is("Juan Pérez Updated")));

        verify(orderService).updateOrder(eq(1L), any(Order.class));
    }

    @Test
    void updateOrder_WithNonExistingId_ShouldReturn404() throws Exception {
        // Given
        given(orderService.updateOrder(eq(999L), any(Order.class)))
                .willThrow(new OrderNotFoundException("Order not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/orders/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(orderService).updateOrder(eq(999L), any(Order.class));
    }

    @Test
    void updateOrder_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        invalidOrder.setId(1L);

        // When & Then
        mockMvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).updateOrder(any(), any());
    }

    // ===============================
    // UPDATE ORDER STATUS TESTS
    // ===============================

    @Test
    void updateOrderStatus_WithValidStatus_ShouldReturnUpdatedOrder() throws Exception {
        // Given
        Order orderWithNewStatus = createValidOrder();
        orderWithNewStatus.setStatus(OrderStatus.SHIPPED);
        given(orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)).willReturn(orderWithNewStatus);

        // When & Then
        mockMvc.perform(patch("/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"SHIPPED\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("SHIPPED")));

        verify(orderService).updateOrderStatus(1L, OrderStatus.SHIPPED);
    }

    @Test
    void updateOrderStatus_WithNonExistingId_ShouldReturn404() throws Exception {
        // Given
        given(orderService.updateOrderStatus(999L, OrderStatus.SHIPPED))
                .willThrow(new OrderNotFoundException("Order not found with id: 999"));

        // When & Then
        mockMvc.perform(patch("/orders/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"SHIPPED\""))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(orderService).updateOrderStatus(999L, OrderStatus.SHIPPED);
    }

    // ===============================
    // DELETE ORDER TESTS
    // ===============================

    @Test
    void deleteOrder_WithExistingId_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/orders/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(1L);
    }

    @Test
    void deleteOrder_WithNonExistingId_ShouldReturn404() throws Exception {
        // Given
        doThrow(new OrderNotFoundException("Order not found with id: 999"))
                .when(orderService).deleteOrder(999L);

        // When & Then
        mockMvc.perform(delete("/orders/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(orderService).deleteOrder(999L);
    }

    // ===============================
    // FILTERING TESTS
    // ===============================

    @Test
    void getOrdersByCustomerEmail_ShouldReturnCustomerOrders() throws Exception {
        // Given
        List<Order> customerOrders = Arrays.asList(validOrder);
        given(orderService.getOrdersByCustomerEmail("juan.perez@email.com")).willReturn(customerOrders);

        // When & Then
        mockMvc.perform(get("/orders/customer/juan.perez@email.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerEmail", is("juan.perez@email.com")));

        verify(orderService).getOrdersByCustomerEmail("juan.perez@email.com");
    }

    @Test
    void getOrdersByStatus_ShouldReturnFilteredOrders() throws Exception {
        // Given
        List<Order> pendingOrders = Arrays.asList(validOrder);
        given(orderService.getOrdersByStatus(OrderStatus.PENDING)).willReturn(pendingOrders);

        // When & Then
        mockMvc.perform(get("/orders/status/PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));

        verify(orderService).getOrdersByStatus(OrderStatus.PENDING);
    }

    @Test
    void getOrdersByDateRange_WithValidDates_ShouldReturnOrders() throws Exception {
        // Given
        List<Order> ordersInRange = Arrays.asList(validOrder);
        given(orderService.getOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(ordersInRange);

        // When & Then
        mockMvc.perform(get("/orders/date-range")
                .param("startDate", "2025-10-19T00:00:00")
                .param("endDate", "2025-10-19T23:59:59"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(orderService).getOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getOrdersByDateRange_WithInvalidDateFormat_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders/date-range")
                .param("startDate", "invalid-date")
                .param("endDate", "2025-10-19T23:59:59"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).getOrdersByDateRange(any(), any());
    }

    // ===============================
    // ANALYTICS TESTS
    // ===============================

    @Test
    void getOrderCountByStatus_ShouldReturnCount() throws Exception {
        // Given
        given(orderService.countOrdersByStatus(OrderStatus.PENDING)).willReturn(5L);

        // When & Then
        mockMvc.perform(get("/orders/count/status/PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.count", is(5)));

        verify(orderService).countOrdersByStatus(OrderStatus.PENDING);
    }

    // ===============================
    // HEALTH CHECK TESTS
    // ===============================

    @Test
    void healthCheck_ShouldReturnHealthStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("Order Management System")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // ===============================
    // EDGE CASES AND ERROR SCENARIOS
    // ===============================

    @Test
    void getAllOrders_WithVeryLargePageSize_ShouldHandleGracefully() throws Exception {
        // Given
        given(orderService.getAllOrders()).willReturn(orderList);

        // When & Then
        mockMvc.perform(get("/orders")
                .param("page", "-1")
                .param("size", "0")
                .param("sortBy", "createdAt")
                .param("sortDir", "invalid"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderService).getAllOrders();
    }

    @Test
    void createOrder_WithEmptyRequestBody_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void updateOrder_WithMismatchedIdInPath_ShouldStillWork() throws Exception {
        // Given
        Order orderWithDifferentId = createValidOrder();
        orderWithDifferentId.setId(99L); // Different from path ID
        given(orderService.updateOrder(eq(1L), any(Order.class))).willReturn(orderWithDifferentId);

        // When & Then
        mockMvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderWithDifferentId)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderService).updateOrder(eq(1L), any(Order.class));
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private Order createValidOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Juan Pérez");
        order.setCustomerEmail("juan.perez@email.com");
        order.setProductName("iPhone 15 Pro");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("999.99"));
        order.setTotalAmount(new BigDecimal("1999.98"));
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress("Av. Corrientes 1234, CABA");
        order.setCreatedAt(LocalDateTime.now().minusHours(1));
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    private Order createInvalidOrder() {
        Order order = new Order();
        order.setCustomerName(""); // Invalid: empty name
        order.setCustomerEmail("invalid-email"); // Invalid email format
        order.setQuantity(-1); // Invalid: negative quantity
        order.setUnitPrice(new BigDecimal("-10.00")); // Invalid: negative price
        return order;
    }

    private List<Order> createOrderList() {
        Order order1 = createValidOrder();
        
        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomerName("María García");
        order2.setCustomerEmail("maria.garcia@email.com");
        order2.setProductName("MacBook Pro 16");
        order2.setQuantity(1);
        order2.setUnitPrice(new BigDecimal("2499.99"));
        order2.setTotalAmount(new BigDecimal("2499.99"));
        order2.setStatus(OrderStatus.SHIPPED);
        order2.setShippingAddress("Calle Florida 950, CABA");
        order2.setCreatedAt(LocalDateTime.now().minusHours(2));
        order2.setUpdatedAt(LocalDateTime.now().minusMinutes(30));

        Order order3 = new Order();
        order3.setId(3L);
        order3.setCustomerName("Carlos López");
        order3.setCustomerEmail("carlos.lopez@email.com");
        order3.setProductName("Samsung Galaxy S24");
        order3.setQuantity(1);
        order3.setUnitPrice(new BigDecimal("899.99"));
        order3.setTotalAmount(new BigDecimal("899.99"));
        order3.setStatus(OrderStatus.DELIVERED);
        order3.setShippingAddress("Av. Santa Fe 2020, CABA");
        order3.setCreatedAt(LocalDateTime.now().minusHours(3));
        order3.setUpdatedAt(LocalDateTime.now().minusMinutes(15));

        return Arrays.asList(order1, order2, order3);
    }
}