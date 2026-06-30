package com.example.ecommerce.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = @UniqueConstraint(name = "uk_notification_order_type", columnNames = {"order_id", "type"})
)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String message;
    private boolean readFlag;
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(String userId, String orderId, NotificationType type, String message) {
        this.userId = userId;
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.readFlag = false;
        this.createdAt = Instant.now();
    }

    public void markRead() {
        readFlag = true;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
