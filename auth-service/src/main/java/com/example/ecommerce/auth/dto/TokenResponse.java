package com.example.ecommerce.auth.dto;

import java.time.Instant;
import java.util.List;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        List<String> roles
) {
}
