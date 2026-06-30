package com.example.ecommerce.notification.dto;

import com.example.ecommerce.notification.entity.Notification;
import com.example.ecommerce.notification.entity.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String userId,
        String orderId,
        NotificationType type,
        String message,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getOrderId(),
                notification.getType(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt()
        );
    }
}
