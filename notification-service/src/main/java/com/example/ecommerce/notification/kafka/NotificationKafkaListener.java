package com.example.ecommerce.notification.kafka;

import com.example.ecommerce.common.event.PaymentAuthorizedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaListener {
    private final NotificationService notificationService;

    public NotificationKafkaListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = Topics.PAYMENT_AUTHORIZED)
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        notificationService.createOrderPaidNotification(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED)
    public void onPaymentFailed(PaymentFailedEvent event) {
        notificationService.createOrderCancelledNotification(event);
    }
}
