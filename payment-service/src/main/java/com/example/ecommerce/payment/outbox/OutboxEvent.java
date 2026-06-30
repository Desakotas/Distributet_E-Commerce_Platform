package com.example.ecommerce.payment.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private String id;

    private String topic;
    private String aggregateId;
    private String payloadType;

    @Lob
    @Column(nullable = false)
    private String payload;

    private Instant createdAt;
    private Instant publishedAt;
    private int attempts;

    @Column(length = 1000)
    private String lastError;

    protected OutboxEvent() {
    }

    public OutboxEvent(String topic, String aggregateId, String payloadType, String payload) {
        this.id = UUID.randomUUID().toString();
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.payloadType = payloadType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(Exception exception) {
        this.attempts++;
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        this.lastError = message.length() > 1000 ? message.substring(0, 1000) : message;
    }

    public String getTopic() {
        return topic;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public String getPayload() {
        return payload;
    }
}
