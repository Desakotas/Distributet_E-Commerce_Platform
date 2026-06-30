package com.example.ecommerce.payment.controller;

import com.example.ecommerce.payment.dto.PaymentResponse;
import com.example.ecommerce.payment.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{orderId}")
    public PaymentResponse getPayment(@PathVariable String orderId, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return paymentService.getPaymentForOrder(orderId, authentication.getName(), admin);
    }
}
