package com.meli.ordermanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class representing an Order in the MELI e-commerce system.
 * 
 * This JPA entity maps to the 'orders' table in the database and encapsulates
 * all essential information for comprehensive order management including customer
 * details, product specifications, pricing calculations, order status tracking,
 * and complete audit trail with timestamps. The entity includes automatic
 * lifecycle callbacks for timestamp management and total amount calculations.
 * 
 * Features:
 * - Automatic total amount calculation based on quantity and unit price
 * - Order status management with predefined enum values
 * - Complete audit trail with creation and update timestamps
 * - Bean validation for data integrity and business rules
 * - JPA lifecycle callbacks for automated field management
 * 
 * @author Melany Rivera
 * @version 1.0.0
 * @since October 16, 2025
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer's full name who placed the order.
     */
    @Column(name = "customer_name", nullable = false, length = 100)
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    /**
     * Customer's email address for order notifications.
     */
    @Column(name = "customer_email", nullable = false, length = 100)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Please provide a valid email address")
    private String customerEmail;

    /**
     * Name of the product being ordered.
     */
    @Column(name = "product_name", nullable = false, length = 200)
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 200, message = "Product name must be between 1 and 200 characters")
    private String productName;

    /**
     * Quantity of the product ordered.
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity cannot exceed 1000")
    private Integer quantity;

    /**
     * Unit price of the product.
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Unit price cannot exceed 999999.99")
    private BigDecimal unitPrice;

    /**
     * Total amount for the order (quantity * unit price).
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Shipping address for the order.
     */
    @Column(name = "shipping_address", nullable = false, length = 500)
    @NotBlank(message = "Shipping address is required")
    @Size(min = 10, max = 500, message = "Shipping address must be between 10 and 500 characters")
    private String shippingAddress;

    /**
     * Timestamp when the order was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Order() {
    }

    /**
     * Constructor for creating a new order.
     *
     * @param customerName     the customer's full name
     * @param customerEmail    the customer's email address
     * @param productName      the name of the product
     * @param quantity         the quantity of the product
     * @param unitPrice        the unit price of the product
     * @param shippingAddress  the shipping address
     */
    public Order(String customerName, String customerEmail, String productName, 
                 Integer quantity, BigDecimal unitPrice, String shippingAddress) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.calculateTotalAmount();
    }

    /**
     * JPA callback method executed before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.calculateTotalAmount();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    /**
     * JPA callback method executed before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.calculateTotalAmount();
    }

    /**
     * Calculates the total amount based on quantity and unit price.
     */
    private void calculateTotalAmount() {
        if (this.quantity != null && this.unitPrice != null) {
            this.totalAmount = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}