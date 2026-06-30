package com.example.ecommerce.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String id;

    private String orderId;
    private String userId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String failureReason;
    private Instant createdAt;

    protected Payment() {
    }

    private Payment(String orderId, String userId, BigDecimal amount, PaymentStatus status, String failureReason) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = Instant.now();
    }

    public static Payment authorized(String orderId, String userId, BigDecimal amount) {
        return new Payment(orderId, userId, amount, PaymentStatus.AUTHORIZED, null);
    }

    public static Payment failed(String orderId, String userId, BigDecimal amount, String reason) {
        return new Payment(orderId, userId, amount, PaymentStatus.FAILED, reason);
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
