package com.example.ecommerce.common.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentAuthorizedEvent(
        String orderId,
        String userId,
        String paymentId,
        BigDecimal amount,
        Instant authorizedAt
) {
}
