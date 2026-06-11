package com.example.devsecopslab.order;

import com.example.devsecopslab.common.CurrentUser;
import com.example.devsecopslab.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable UUID id,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-ROLE", defaultValue = "USER") String role
    ) {
        CurrentUser currentUser = currentUserResolver.resolve(userId, role);
        return ResponseEntity.ok(orderService.getOrder(id, currentUser));
    }
}
