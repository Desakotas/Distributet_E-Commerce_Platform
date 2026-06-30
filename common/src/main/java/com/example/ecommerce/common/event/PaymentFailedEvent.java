package com.example.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFailedEvent(
        String orderId,
        String userId,
        BigDecimal amount,
        String reason,
        Instant failedAt
) {
}
