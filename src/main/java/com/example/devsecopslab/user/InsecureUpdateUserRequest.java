package com.example.devsecopslab.user;

import java.math.BigDecimal;

/**
 * LAB ONLY:
 * This request intentionally contains fields a client must not be allowed to update.
 */
public record InsecureUpdateUserRequest(
        String fullName,
        String email,
        Role role,
        Boolean verified,
        BigDecimal balance
) {}
