package com.example.devsecopslab.user;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * LAB ONLY:
 * This DTO intentionally exposes sensitive fields to demonstrate Excessive Data Exposure.
 */
public record InsecureUserResponse(
        UUID id,
        String email,
        String fullName,
        String passwordHash,
        Role role,
        boolean verified,
        BigDecimal balance
) {
    public static InsecureUserResponse from(User user) {
        return new InsecureUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPasswordHash(),
                user.getRole(),
                user.isVerified(),
                user.getBalance()
        );
    }
}
