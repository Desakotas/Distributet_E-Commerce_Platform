package com.example.ecommerce.order.service;

import com.example.ecommerce.common.event.OrderCreatedEvent;
import com.example.ecommerce.common.event.OrderItemMessage;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.order.dto.CheckoutRequest;
import com.example.ecommerce.order.dto.OrderResponse;
import com.example.ecommerce.order.entity.OrderEntity;
import com.example.ecommerce.order.outbox.OutboxService;
import com.example.ecommerce.order.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    public OrderService(OrderRepository orderRepository, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public OrderResponse checkout(String userId, CheckoutRequest request) {
        String paymentMode = request.paymentMode() == null ? "MOCK_OK" : request.paymentMode();
        OrderEntity order = new OrderEntity(UUID.randomUUID().toString(), userId, paymentMode);
        request.items().forEach(item -> order.addItem(item.productId(), item.quantity(), money(item.unitPrice())));

        OrderEntity saved = orderRepository.save(order);
        outboxService.save(Topics.ORDER_CREATED, saved.getId(), toOrderCreatedEvent(saved));
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderId, String currentUserId, boolean admin) {
        OrderEntity order = findOrder(orderId);
        if (!admin && !order.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot read another user's order");
        }
        return OrderResponse.from(order);
    }

    @Transactional
    public void markInventoryReserved(String orderId) {
        findOrder(orderId).markInventoryReserved();
    }

    @Transactional
    public void markPaid(String orderId) {
        findOrder(orderId).markPaid();
    }

    @Transactional
    public void cancel(String orderId, String reason) {
        findOrder(orderId).markCancelled(reason);
    }

    private OrderEntity findOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private OrderCreatedEvent toOrderCreatedEvent(OrderEntity order) {
        List<OrderItemMessage> items = order.getItems().stream()
                .map(item -> new OrderItemMessage(item.getProductId(), item.getQuantity(), money(item.getUnitPrice())))
                .toList();
        return new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                items,
                money(order.getTotalAmount()),
                order.getPaymentMode(),
                Instant.now()
        );
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
