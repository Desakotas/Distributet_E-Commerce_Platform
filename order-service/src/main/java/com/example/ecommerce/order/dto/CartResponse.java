package com.example.ecommerce.order.dto;

import com.example.ecommerce.order.entity.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record CartResponse(
        String userId,
        BigDecimal totalAmount,
        List<CartItemResponse> items
) {
    public static CartResponse from(String userId, List<CartItem> items) {
        List<CartItemResponse> responses = items.stream()
                .map(CartItemResponse::from)
                .toList();
        BigDecimal totalAmount = responses.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(userId, money(totalAmount), responses);
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
