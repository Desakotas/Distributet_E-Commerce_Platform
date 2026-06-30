package com.example.ecommerce.order.dto;

import com.example.ecommerce.order.entity.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CartItemResponse(
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
    public static CartItemResponse from(CartItem item) {
        BigDecimal unitPrice = money(item.getUnitPrice());
        return new CartItemResponse(
                item.getProductId(),
                item.getQuantity(),
                unitPrice,
                money(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
        );
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
