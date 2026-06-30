package com.example.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CheckoutRequest(
        @NotEmpty List<@Valid CheckoutItemRequest> items,
        @Pattern(regexp = "MOCK_OK|MOCK_FAIL", message = "paymentMode must be MOCK_OK or MOCK_FAIL")
        String paymentMode
) {
}
