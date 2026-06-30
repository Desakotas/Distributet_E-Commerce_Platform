package com.example.ecommerce.notification.repository;

import com.example.ecommerce.notification.entity.Notification;
import com.example.ecommerce.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Notification> findByIdAndUserId(Long id, String userId);

    boolean existsByOrderIdAndType(String orderId, NotificationType type);
}
