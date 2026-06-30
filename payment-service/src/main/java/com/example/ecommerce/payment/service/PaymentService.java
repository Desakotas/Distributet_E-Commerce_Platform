package com.example.ecommerce.payment.service;

import com.example.ecommerce.common.event.InventoryReservedEvent;
import com.example.ecommerce.common.event.PaymentAuthorizedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.payment.dto.PaymentResponse;
import com.example.ecommerce.payment.entity.Payment;
import com.example.ecommerce.payment.outbox.OutboxService;
import com.example.ecommerce.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;

    public PaymentService(PaymentRepository paymentRepository, OutboxService outboxService) {
        this.paymentRepository = paymentRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public void authorize(InventoryReservedEvent event) {
        if (paymentRepository.existsByOrderId(event.orderId())) {
            return;
        }

        if ("MOCK_FAIL".equalsIgnoreCase(event.paymentMode())) {
            Payment payment = paymentRepository.save(Payment.failed(
                    event.orderId(),
                    event.userId(),
                    event.totalAmount(),
                    "Mock payment gateway rejected the charge"
            ));
            outboxService.save(Topics.PAYMENT_FAILED, event.orderId(), new PaymentFailedEvent(
                    event.orderId(),
                    event.userId(),
                    payment.getAmount(),
                    payment.getFailureReason(),
                    Instant.now()
            ));
            return;
        }

        Payment payment = paymentRepository.save(Payment.authorized(
                event.orderId(),
                event.userId(),
                event.totalAmount()
        ));
        outboxService.save(Topics.PAYMENT_AUTHORIZED, event.orderId(), new PaymentAuthorizedEvent(
                event.orderId(),
                event.userId(),
                payment.getId(),
                payment.getAmount(),
                Instant.now()
        ));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForOrder(String orderId, String currentUserId, boolean admin) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (!admin && !payment.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot read another user's payment");
        }
        return PaymentResponse.from(payment);
    }
}
