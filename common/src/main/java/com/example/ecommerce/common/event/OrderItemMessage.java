package com.example.ecommerce.common.event;

import java.math.BigDecimal;

public record OrderItemMessage(
        Long productId,
        int quantity,
        BigDecimal unitPrice
) {
}
