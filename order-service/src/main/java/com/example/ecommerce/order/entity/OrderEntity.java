package com.example.ecommerce.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
public class OrderEntity {
    @Id
    private String id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;
    private String paymentMode;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderLineEntity> items = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(String id, String userId, String paymentMode) {
        this.id = id;
        this.userId = userId;
        this.paymentMode = paymentMode;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void addItem(Long productId, int quantity, BigDecimal unitPrice) {
        OrderLineEntity line = new OrderLineEntity(this, productId, quantity, unitPrice);
        items.add(line);
        totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        touch();
    }

    public void markInventoryReserved() {
        if (status == OrderStatus.PENDING) {
            status = OrderStatus.INVENTORY_RESERVED;
            touch();
        }
    }

    public void markPaid() {
        status = OrderStatus.PAID;
        failureReason = null;
        touch();
    }

    public void markCancelled(String reason) {
        if (status != OrderStatus.PAID) {
            status = OrderStatus.CANCELLED;
            failureReason = reason;
            touch();
        }
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<OrderLineEntity> getItems() {
        return items;
    }
}
