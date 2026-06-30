package com.example.ecommerce.notification.service;

import com.example.ecommerce.common.event.PaymentAuthorizedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.notification.dto.NotificationResponse;
import com.example.ecommerce.notification.entity.Notification;
import com.example.ecommerce.notification.entity.NotificationType;
import com.example.ecommerce.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void createOrderPaidNotification(PaymentAuthorizedEvent event) {
        if (notificationRepository.existsByOrderIdAndType(event.orderId(), NotificationType.ORDER_PAID)) {
            return;
        }
        notificationRepository.save(new Notification(
                event.userId(),
                event.orderId(),
                NotificationType.ORDER_PAID,
                "Order " + event.orderId() + " has been paid successfully."
        ));
    }

    @Transactional
    public void createOrderCancelledNotification(PaymentFailedEvent event) {
        if (notificationRepository.existsByOrderIdAndType(event.orderId(), NotificationType.ORDER_CANCELLED)) {
            return;
        }
        notificationRepository.save(new Notification(
                event.userId(),
                event.orderId(),
                NotificationType.ORDER_CANCELLED,
                "Order " + event.orderId() + " was cancelled because payment failed: " + event.reason()
        ));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> myNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public NotificationResponse markRead(Long id, String userId) {
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.markRead();
        return NotificationResponse.from(notification);
    }
}
