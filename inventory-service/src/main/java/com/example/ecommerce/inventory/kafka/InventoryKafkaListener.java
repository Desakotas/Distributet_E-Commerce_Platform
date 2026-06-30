package com.example.ecommerce.inventory.kafka;

import com.example.ecommerce.common.event.OrderCreatedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryKafkaListener {
    private final InventoryService inventoryService;

    public InventoryKafkaListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED)
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserveForOrder(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        inventoryService.releaseForFailedPayment(event);
    }
}
