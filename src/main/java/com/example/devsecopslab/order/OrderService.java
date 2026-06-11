package com.example.devsecopslab.order;

import com.example.devsecopslab.common.CurrentUser;
import com.example.devsecopslab.config.LabProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSecurity orderSecurity;
    private final LabProperties labProperties;

    public OrderResponse getOrder(UUID orderId, CurrentUser currentUser) {
        if (!labProperties.isVulnerableMode()) {
            boolean allowed = orderSecurity.canReadOrder(currentUser, orderId);

            if (!allowed) {
                log.warn("Unauthorized order access attempt requesterId={} orderId={}",
                        currentUser.id(), orderId);
                throw new AccessDeniedException("Forbidden");
            }
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return OrderResponse.from(order);
    }
}
