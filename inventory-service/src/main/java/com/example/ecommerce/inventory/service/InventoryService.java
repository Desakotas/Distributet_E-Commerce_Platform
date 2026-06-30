package com.example.ecommerce.inventory.service;

import com.example.ecommerce.common.event.InventoryRejectedEvent;
import com.example.ecommerce.common.event.InventoryReservedEvent;
import com.example.ecommerce.common.event.OrderCreatedEvent;
import com.example.ecommerce.common.event.PaymentFailedEvent;
import com.example.ecommerce.common.event.Topics;
import com.example.ecommerce.inventory.dto.InventoryResponse;
import com.example.ecommerce.inventory.dto.RestockRequest;
import com.example.ecommerce.inventory.entity.InventoryItem;
import com.example.ecommerce.inventory.entity.InventoryReservation;
import com.example.ecommerce.inventory.outbox.OutboxService;
import com.example.ecommerce.inventory.repository.InventoryItemRepository;
import com.example.ecommerce.inventory.repository.InventoryReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryItemRepository itemRepository;
    private final InventoryReservationRepository reservationRepository;
    private final OutboxService outboxService;

    public InventoryService(
            InventoryItemRepository itemRepository,
            InventoryReservationRepository reservationRepository,
            OutboxService outboxService
    ) {
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory() {
        return itemRepository.findAll().stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {
        return InventoryResponse.from(findItem(productId));
    }

    @Transactional
    public InventoryResponse restock(RestockRequest request) {
        InventoryItem item = itemRepository.findById(request.productId())
                .orElseGet(() -> new InventoryItem(request.productId(), 0));
        item.restock(request.quantity());
        return InventoryResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void reserveForOrder(OrderCreatedEvent event) {
        if (reservationRepository.existsByOrderId(event.orderId())) {
            return;
        }

        Map<Long, Integer> requiredByProduct = event.items().stream()
                .collect(Collectors.groupingBy(
                        item -> item.productId(),
                        LinkedHashMap::new,
                        Collectors.summingInt(item -> item.quantity())
                ));

        Map<Long, InventoryItem> lockedItems = new LinkedHashMap<>();
        for (Long productId : requiredByProduct.keySet()) {
            InventoryItem item = itemRepository.findLockedByProductId(productId).orElse(null);
            if (item == null) {
                outboxService.save(Topics.INVENTORY_REJECTED, event.orderId(), new InventoryRejectedEvent(
                        event.orderId(),
                        "No inventory row for product " + productId,
                        Instant.now()
                ));
                return;
            }
            lockedItems.put(productId, item);
        }

        for (Map.Entry<Long, Integer> required : requiredByProduct.entrySet()) {
            InventoryItem item = lockedItems.get(required.getKey());
            if (item.getAvailableQuantity() < required.getValue()) {
                outboxService.save(Topics.INVENTORY_REJECTED, event.orderId(), new InventoryRejectedEvent(
                        event.orderId(),
                        "Insufficient inventory for product " + required.getKey(),
                        Instant.now()
                ));
                return;
            }
        }

        requiredByProduct.forEach((productId, quantity) -> {
            lockedItems.get(productId).reserve(quantity);
            reservationRepository.save(new InventoryReservation(event.orderId(), productId, quantity));
        });

        outboxService.save(Topics.INVENTORY_RESERVED, event.orderId(), new InventoryReservedEvent(
                event.orderId(),
                event.userId(),
                event.items(),
                event.totalAmount(),
                event.paymentMode(),
                Instant.now()
        ));
    }

    @Transactional
    public void releaseForFailedPayment(PaymentFailedEvent event) {
        List<InventoryReservation> reservations = reservationRepository.findByOrderIdAndReleasedFalse(event.orderId());
        for (InventoryReservation reservation : reservations) {
            InventoryItem item = itemRepository.findLockedByProductId(reservation.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Inventory item not found"));
            item.release(reservation.getQuantity());
            reservation.markReleased();
        }
    }

    private InventoryItem findItem(Long productId) {
        return itemRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found"));
    }
}
