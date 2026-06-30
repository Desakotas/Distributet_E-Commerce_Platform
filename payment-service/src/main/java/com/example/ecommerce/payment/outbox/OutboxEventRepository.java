package com.example.ecommerce.payment.outbox;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxEvent> findTop20ByPublishedAtIsNullAndAttemptsLessThanOrderByCreatedAtAsc(int maxAttempts);
}
