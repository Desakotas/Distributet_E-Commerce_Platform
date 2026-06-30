package com.example.ecommerce.order.repository;

import com.example.ecommerce.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdOrderByCreatedAtAsc(String userId);

    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    long deleteByUserIdAndProductId(String userId, Long productId);

    void deleteByUserId(String userId);
}
