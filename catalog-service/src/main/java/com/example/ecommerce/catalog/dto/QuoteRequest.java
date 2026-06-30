package com.example.ecommerce.catalog.dto;

import jakarta.validation.constraints.Min;

public record QuoteRequest(
        @Min(1) int quantity,
        String customerTier
) {
}
