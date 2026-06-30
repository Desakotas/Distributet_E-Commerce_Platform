package com.example.ecommerce.order.dto;

import com.example.ecommerce.order.entity.OrderEntity;
import com.example.ecommerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        OrderStatus status,
        BigDecimal totalAmount,
        String paymentMode,
        String failureReason,
        Instant createdAt,
        Instant updatedAt,
        List<OrderLineResponse> items
) {
    public static OrderResponse from(OrderEntity entity) {
        return new OrderResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getStatus(),
                entity.getTotalAmount(),
                entity.getPaymentMode(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getItems().stream().map(OrderLineResponse::from).toList()
        );
    }
}
