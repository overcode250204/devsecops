package com.example.devsecopslab.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByIdAndUserId(UUID orderId, UUID userId);
}
