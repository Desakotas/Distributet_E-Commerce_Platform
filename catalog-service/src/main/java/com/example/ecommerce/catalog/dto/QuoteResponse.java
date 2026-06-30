package com.example.ecommerce.catalog.dto;

import java.math.BigDecimal;

public record QuoteResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        BigDecimal discountRate,
        BigDecimal discountAmount,
        BigDecimal finalPrice,
        String rule
) {
}
