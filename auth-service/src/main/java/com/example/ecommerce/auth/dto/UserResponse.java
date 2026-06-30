package com.example.ecommerce.auth.dto;

import com.example.ecommerce.auth.entity.UserAccount;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        List<String> roles,
        Instant createdAt
) {
    public static UserResponse from(UserAccount user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRoles(), user.getCreatedAt());
    }
}
