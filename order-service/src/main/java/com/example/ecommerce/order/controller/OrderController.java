package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.dto.CheckoutRequest;
import com.example.ecommerce.order.dto.CheckoutFromCartRequest;
import com.example.ecommerce.order.dto.OrderResponse;
import com.example.ecommerce.order.service.CartService;
import com.example.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(Authentication authentication, @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(authentication.getName(), request);
    }

    @PostMapping("/checkout-from-cart")
    public OrderResponse checkoutFromCart(
            Authentication authentication,
            @Valid @RequestBody CheckoutFromCartRequest request
    ) {
        return cartService.checkoutFromCart(authentication.getName(), request);
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(Authentication authentication) {
        return orderService.myOrders(authentication.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable String id, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return orderService.getOrder(id, authentication.getName(), admin);
    }
}
