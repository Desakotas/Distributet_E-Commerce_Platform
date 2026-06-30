package com.example.ecommerce.order.config;

import com.example.ecommerce.common.event.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    NewTopic orderCreatedTopic() {
        return TopicBuilder.name(Topics.ORDER_CREATED).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic inventoryReservedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_RESERVED).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic inventoryRejectedTopic() {
        return TopicBuilder.name(Topics.INVENTORY_REJECTED).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentAuthorizedTopic() {
        return TopicBuilder.name(Topics.PAYMENT_AUTHORIZED).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentFailedTopic() {
        return TopicBuilder.name(Topics.PAYMENT_FAILED).partitions(3).replicas(1).build();
    }
}
