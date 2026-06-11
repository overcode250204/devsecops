package com.example.devsecopslab.common;

import com.example.devsecopslab.user.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class CurrentUserResolver {

    public CurrentUser resolve(String userIdHeader, String roleHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-USER-ID header");
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);
            Role role = Role.valueOf(roleHeader == null ? "USER" : roleHeader.toUpperCase());

            return new CurrentUser(userId, role);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid auth headers");
        }
    }
}
