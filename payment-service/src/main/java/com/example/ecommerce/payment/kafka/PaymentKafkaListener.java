package com.example.ecommerce.payment.kafka;

import com.example.ecommerce.common.event.InventoryReservedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.payment.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaListener {
    private final PaymentService paymentService;

    public PaymentKafkaListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED)
    public void onInventoryReserved(InventoryReservedEvent event) {
        paymentService.authorize(event);
    }
}
