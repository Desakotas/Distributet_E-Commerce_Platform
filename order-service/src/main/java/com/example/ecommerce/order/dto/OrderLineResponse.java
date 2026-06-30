package com.example.ecommerce.order.dto;

import com.example.ecommerce.order.entity.OrderLineEntity;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long productId,
        int quantity,
        BigDecimal unitPrice
) {
    public static OrderLineResponse from(OrderLineEntity entity) {
        return new OrderLineResponse(entity.getProductId(), entity.getQuantity(), entity.getUnitPrice());
    }
}
