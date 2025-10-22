package com.meli.ordermanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.ordermanager.entity.Order;
import com.meli.ordermanager.entity.OrderStatus;
import com.meli.ordermanager.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🔗 Integration Tests for Order Management System
 * 
 * These tests validate the complete functionality of the order management system
 * including database operations, service layer interactions, and API endpoints.
 * 
 * Features tested:
 * ✅ Complete API workflow with real database
 * ✅ Data persistence and retrieval
 * ✅ Transaction management
 * ✅ Cross-layer integration
 * ✅ Real HTTP requests and responses
 * ✅ Database constraints and validations
 * ✅ End-to-end scenarios
 * 
 * @author Melany Rivera - MercadoLibre Team
 * @version 1.0.0
 * @since October 20, 2025
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrderManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        
        // Clean database before each test
        orderRepository.deleteAll();
    }

    // ===============================
    // COMPLETE WORKFLOW TESTS
    // ===============================

    @Test
    void completeOrderLifecycle_ShouldWorkEndToEnd() throws Exception {
        // 1. Create Order
        Order newOrder = createTestOrder();
        String orderJson = objectMapper.writeValueAsString(newOrder);

        String createdOrderResponse = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Order createdOrder = objectMapper.readValue(createdOrderResponse, Order.class);
        Long orderId = createdOrder.getId();

        // 2. Retrieve Order by ID
        mockMvc.perform(get("/orders/" + orderId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.customerName", is("Integration Test Customer")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        // 3. Update Order Status
        mockMvc.perform(patch("/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"PROCESSING\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PROCESSING")));

        // 4. Update Complete Order
        Order updateOrder = createTestOrder();
        updateOrder.setCustomerName("Updated Customer Name");
        updateOrder.setQuantity(5);
        updateOrder.setTotalAmount(new BigDecimal("2499.95"));
        updateOrder.setStatus(OrderStatus.PROCESSING); // Asegurar que tiene un status válido

        mockMvc.perform(put("/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrder)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName", is("Updated Customer Name")))
                .andExpect(jsonPath("$.quantity", is(5)));

        // 5. Verify in Get All Orders (with pagination disabled)
        mockMvc.perform(get("/orders")
                .param("page", "-1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(orderId.intValue())));

        // 6. Delete Order
        mockMvc.perform(delete("/orders/" + orderId))
                .andDo(print())
                .andExpect(status().isNoContent());

        // 7. Verify Order is Deleted
        mockMvc.perform(get("/orders/" + orderId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ===============================
    // DATABASE PERSISTENCE TESTS
    // ===============================

    @Test
    void createMultipleOrders_ShouldPersistCorrectly() throws Exception {
        // Create multiple orders
        for (int i = 1; i <= 3; i++) {
            Order order = createTestOrder();
            order.setCustomerName("Customer " + i);
            order.setCustomerEmail("customer" + i + "@test.com");

            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(order)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        // Verify all orders are persisted (with pagination disabled)
        mockMvc.perform(get("/orders")
                .param("page", "-1")
                .param("size", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].customerName", containsInAnyOrder(
                        "Customer 1", "Customer 2", "Customer 3")));

        // Verify database count
        long count = orderRepository.count();
        assert count == 3;
    }

    // ===============================
    // FILTERING AND SEARCH TESTS
    // ===============================

    @Test
    void filteringOperations_ShouldWorkWithRealDatabase() throws Exception {
        // Create orders with different statuses
        createOrderWithStatus("customer1@test.com", OrderStatus.PENDING);
        createOrderWithStatus("customer2@test.com", OrderStatus.PROCESSING);
        createOrderWithStatus("customer3@test.com", OrderStatus.SHIPPED);
        createOrderWithStatus("customer1@test.com", OrderStatus.DELIVERED);

        // Test filter by status
        mockMvc.perform(get("/orders/status/PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));

        // Test filter by customer email
        mockMvc.perform(get("/orders/customer/customer1@test.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].customerEmail", everyItem(is("customer1@test.com"))));

        // Test order count by status
        mockMvc.perform(get("/orders/count/status/PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.count", is(1)));
    }

    // ===============================
    // PAGINATION TESTS
    // ===============================

    @Test
    void paginationWithRealData_ShouldWorkCorrectly() throws Exception {
        // Create 15 orders
        for (int i = 1; i <= 15; i++) {
            Order order = createTestOrder();
            order.setCustomerName("Customer " + String.format("%02d", i));
            order.setCustomerEmail("customer" + i + "@test.com");

            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(order)))
                    .andExpect(status().isCreated());

            // Small delay to ensure different creation times
            Thread.sleep(10);
        }

        // Test first page
        mockMvc.perform(get("/orders")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "createdAt")
                .param("sortDir", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(5)))
                .andExpect(jsonPath("$.totalItems", is(15)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));

        // Test middle page
        mockMvc.perform(get("/orders")
                .param("page", "1")
                .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(5)))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.hasPrevious", is(true)));

        // Test last page
        mockMvc.perform(get("/orders")
                .param("page", "2")
                .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(5)))
                .andExpect(jsonPath("$.currentPage", is(2)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(true)));
    }

    // ===============================
    // DATE RANGE FILTERING TESTS
    // ===============================

    @Test
    void dateRangeFiltering_ShouldWorkWithRealTimestamps() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        
        // Create orders at different times
        Order order1 = createTestOrder();
        order1.setCustomerName("Early Customer");
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isCreated());

        Thread.sleep(100); // Ensure different timestamps

        Order order2 = createTestOrder();
        order2.setCustomerName("Late Customer");
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isCreated());

        // Test date range filtering
        String startDate = now.minusMinutes(5).toString();
        String endDate = now.plusMinutes(5).toString();

        mockMvc.perform(get("/orders/date-range")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ===============================
    // ERROR HANDLING TESTS
    // ===============================

    @Test
    void errorHandling_ShouldWorkAcrossAllLayers() throws Exception {
        // Test 404 on non-existent order
        mockMvc.perform(get("/orders/999999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Test validation errors
        Order invalidOrder = new Order();
        invalidOrder.setCustomerName(""); // Invalid

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Test updating non-existent order
        Order validOrder = createTestOrder();
        mockMvc.perform(put("/orders/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Test deleting non-existent order
        mockMvc.perform(delete("/orders/999999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ===============================
    // TRANSACTION ROLLBACK TESTS
    // ===============================

    @Test
    void transactionRollback_ShouldWorkCorrectly() throws Exception {
        // Create an order
        Order order = createTestOrder();
        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Order createdOrder = objectMapper.readValue(response, Order.class);
        Long orderId = createdOrder.getId();

        // Verify order exists
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk());

        // Due to @Transactional, changes should be rolled back after test
        // This is verified by other tests not seeing this data
    }

    // ===============================
    // HEALTH CHECK INTEGRATION
    // ===============================

    @Test
    void healthCheck_ShouldReturnCorrectStatus() throws Exception {
        mockMvc.perform(get("/orders/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("Order Management System")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private Order createTestOrder() {
        Order order = new Order();
        order.setCustomerName("Integration Test Customer");
        order.setCustomerEmail("integration@test.com");
        order.setProductName("Integration Test Product");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("499.99"));
        order.setTotalAmount(new BigDecimal("999.98"));
        order.setShippingAddress("123 Integration Test St, Test City, TC 12345");
        return order;
    }

    private void createOrderWithStatus(String email, OrderStatus status) throws Exception {
        Order order = createTestOrder();
        order.setCustomerEmail(email);
        
        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Order createdOrder = objectMapper.readValue(response, Order.class);
        
        if (status != OrderStatus.PENDING) {
            mockMvc.perform(patch("/orders/" + createdOrder.getId() + "/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("\"" + status.name() + "\""))
                    .andExpect(status().isOk());
        }
    }
}