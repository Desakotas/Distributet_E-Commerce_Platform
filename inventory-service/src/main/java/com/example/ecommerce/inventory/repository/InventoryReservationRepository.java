package com.example.ecommerce.inventory.repository;

import com.example.ecommerce.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    boolean existsByOrderId(String orderId);

    List<InventoryReservation> findByOrderIdAndReleasedFalse(String orderId);
}
