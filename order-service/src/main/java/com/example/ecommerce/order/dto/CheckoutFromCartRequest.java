package com.example.ecommerce.order.dto;

import jakarta.validation.constraints.Pattern;

public record CheckoutFromCartRequest(
        @Pattern(regexp = "MOCK_OK|MOCK_FAIL", message = "paymentMode must be MOCK_OK or MOCK_FAIL")
        String paymentMode
) {
}
