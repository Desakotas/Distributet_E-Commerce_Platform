package com.example.ecommerce.order.kafka;

import com.example.ecommerce.common.event.InventoryRejectedEvent;
import com.example.ecommerce.common.event.InventoryReservedEvent;
import com.example.ecommerce.common.event.PaymentAuthorizedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaListener {
    private final OrderService orderService;

    public OrderSagaListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED)
    public void onInventoryReserved(InventoryReservedEvent event) {
        orderService.markInventoryReserved(event.orderId());
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED)
    public void onInventoryRejected(InventoryRejectedEvent event) {
        orderService.cancel(event.orderId(), event.reason());
    }

    @KafkaListener(topics = Topics.PAYMENT_AUTHORIZED)
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        orderService.markPaid(event.orderId());
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        orderService.cancel(event.orderId(), event.reason());
    }
}
