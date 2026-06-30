package com.example.ecommerce.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestockRequest(
        @NotNull Long productId,
        @Min(1) int quantity
) {
}
