package com.example.devsecopslab.user;

import com.example.devsecopslab.common.CurrentUser;
import com.example.devsecopslab.common.CurrentUserResolver;
import com.example.devsecopslab.config.LabProperties;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;
    private final LabProperties labProperties;

    @GetMapping("/search")
    public ResponseEntity<List<?>> searchByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(userService.getUsers(sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable UUID id,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-ROLE", defaultValue = "USER") String role
    ) {
        CurrentUser currentUser = currentUserResolver.resolve(userId, role);
        return ResponseEntity.ok(userService.getUser(id, currentUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-ROLE", defaultValue = "USER") String role,
            @Valid @RequestBody InsecureUpdateUserRequest insecureRequest
    ) {
        CurrentUser currentUser = currentUserResolver.resolve(userId, role);

        if (labProperties.isVulnerableMode()) {
            return ResponseEntity.ok(userService.updateUserVulnerable(id, insecureRequest));
        }

        UpdateProfileRequest request = new UpdateProfileRequest(
                insecureRequest.fullName(),
                insecureRequest.email()
        );

        return ResponseEntity.ok(userService.updateProfile(id, request, currentUser));
    }
}
