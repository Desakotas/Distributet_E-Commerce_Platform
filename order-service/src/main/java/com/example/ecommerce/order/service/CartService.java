package com.example.ecommerce.order.service;

import com.example.ecommerce.order.dto.AddCartItemRequest;
import com.example.ecommerce.order.dto.CartResponse;
import com.example.ecommerce.order.dto.CheckoutFromCartRequest;
import com.example.ecommerce.order.dto.CheckoutItemRequest;
import com.example.ecommerce.order.dto.CheckoutRequest;
import com.example.ecommerce.order.dto.OrderResponse;
import com.example.ecommerce.order.dto.UpdateCartItemRequest;
import com.example.ecommerce.order.entity.CartItem;
import com.example.ecommerce.order.repository.CartItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public CartService(CartItemRepository cartItemRepository, OrderService orderService) {
        this.cartItemRepository = cartItemRepository;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        return toCartResponse(userId);
    }

    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        BigDecimal unitPrice = money(request.unitPrice());
        cartItemRepository.findByUserIdAndProductId(userId, request.productId())
                .ifPresentOrElse(
                        item -> item.addQuantity(request.quantity(), unitPrice),
                        () -> cartItemRepository.save(new CartItem(
                                userId,
                                request.productId(),
                                request.quantity(),
                                unitPrice
                        ))
                );
        return toCartResponse(userId);
    }

    @Transactional
    public CartResponse updateItem(String userId, Long productId, UpdateCartItemRequest request) {
        CartItem item = findCartItem(userId, productId);
        item.updateQuantity(request.quantity());
        return toCartResponse(userId);
    }

    @Transactional
    public CartResponse removeItem(String userId, Long productId) {
        long deleted = cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
        return toCartResponse(userId);
    }

    @Transactional
    public CartResponse clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
        return toCartResponse(userId);
    }

    @Transactional
    public OrderResponse checkoutFromCart(String userId, CheckoutFromCartRequest request) {
        List<CartItem> items = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        List<CheckoutItemRequest> checkoutItems = items.stream()
                .map(item -> new CheckoutItemRequest(
                        item.getProductId(),
                        item.getQuantity(),
                        money(item.getUnitPrice())
                ))
                .toList();

        OrderResponse order = orderService.checkout(userId, new CheckoutRequest(checkoutItems, request.paymentMode()));
        cartItemRepository.deleteByUserId(userId);
        return order;
    }

    private CartItem findCartItem(String userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
    }

    private CartResponse toCartResponse(String userId) {
        return CartResponse.from(userId, cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
