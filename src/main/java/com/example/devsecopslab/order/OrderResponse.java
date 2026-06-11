package com.example.devsecopslab.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        BigDecimal totalAmount,
        String status,
        Instant createdAt,
        UUID ownerId
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUser().getId()
        );
    }
}
