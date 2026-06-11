package com.example.devsecopslab.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInMinutes
) {}
