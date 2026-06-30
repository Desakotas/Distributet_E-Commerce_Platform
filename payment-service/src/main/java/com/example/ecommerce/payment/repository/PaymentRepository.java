package com.example.ecommerce.payment.repository;

import com.example.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByOrderId(String orderId);

    Optional<Payment> findByOrderId(String orderId);
}
