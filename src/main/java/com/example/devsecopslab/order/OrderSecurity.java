package com.example.devsecopslab.order;

import com.example.devsecopslab.common.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean canReadOrder(CurrentUser currentUser, UUID orderId) {
        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAdmin()) {
            return true;
        }

        return orderRepository.existsByIdAndUserId(orderId, currentUser.id());
    }
}
