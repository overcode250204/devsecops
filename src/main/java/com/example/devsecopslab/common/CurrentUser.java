package com.example.devsecopslab.common;

import com.example.devsecopslab.user.Role;

import java.util.UUID;

public record CurrentUser(
        UUID id,
        Role role
) {
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
