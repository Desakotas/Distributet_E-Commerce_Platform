package com.example.ecommerce.common.event;

import java.time.Instant;

public record InventoryRejectedEvent(
        String orderId,
        String reason,
        Instant rejectedAt
) {
}
