package com.example.ecommerce.order.controller;

import com.example.ecommerce.order.dto.AddCartItemRequest;
import com.example.ecommerce.order.dto.CartResponse;
import com.example.ecommerce.order.dto.UpdateCartItemRequest;
import com.example.ecommerce.order.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public CartResponse addItem(Authentication authentication, @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(authentication.getName(), request);
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateItem(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(authentication.getName(), productId, request);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(Authentication authentication, @PathVariable Long productId) {
        return cartService.removeItem(authentication.getName(), productId);
    }

    @DeleteMapping
    public CartResponse clearCart(Authentication authentication) {
        return cartService.clearCart(authentication.getName());
    }
}
