package com.example.ecommerce.order.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final int maxAttempts;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.outbox.max-attempts:10}") int maxAttempts
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher-delay-ms:2000}")
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEvent event : outboxEventRepository.findTop20ByPublishedAtIsNullAndAttemptsLessThanOrderByCreatedAtAsc(maxAttempts)) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), Class.forName(event.getPayloadType()));
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), payload).get(5, TimeUnit.SECONDS);
                event.markPublished();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                event.markFailed(ex);
                return;
            } catch (Exception ex) {
                event.markFailed(ex);
            }
        }
    }
}
