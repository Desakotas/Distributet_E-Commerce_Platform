package com.example.ecommerce.payment.dto;

import com.example.ecommerce.payment.entity.Payment;
import com.example.ecommerce.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String id,
        String orderId,
        String userId,
        BigDecimal amount,
        PaymentStatus status,
        String failureReason,
        Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
