package com.example.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InventoryReservedEvent(
        String orderId,
        String userId,
        List<OrderItemMessage> items,
        BigDecimal totalAmount,
        String paymentMode,
        Instant reservedAt
) {
}
