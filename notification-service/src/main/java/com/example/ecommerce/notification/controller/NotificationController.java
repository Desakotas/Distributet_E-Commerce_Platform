package com.example.ecommerce.notification.controller;

import com.example.ecommerce.notification.dto.NotificationResponse;
import com.example.ecommerce.notification.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    public List<NotificationResponse> myNotifications(Authentication authentication) {
        return notificationService.myNotifications(authentication.getName());
    }

    @PostMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id, Authentication authentication) {
        return notificationService.markRead(id, authentication.getName());
    }
}
