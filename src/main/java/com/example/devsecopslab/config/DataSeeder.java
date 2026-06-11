package com.example.devsecopslab.config;

import com.example.devsecopslab.order.Order;
import com.example.devsecopslab.order.OrderRepository;
import com.example.devsecopslab.order.OrderStatus;
import com.example.devsecopslab.user.Role;
import com.example.devsecopslab.user.User;
import com.example.devsecopslab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    public static final UUID USER_A_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID USER_B_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID ADMIN_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    public static final UUID ORDER_A_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    public static final UUID ORDER_B_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        User userA = User.builder()
                .id(USER_A_ID)
                .email("userA@example.com")
                .fullName("User A")
                .passwordHash(encoder.encode("Password@123"))
                .role(Role.USER)
                .verified(false)
                .balance(new BigDecimal("100000"))
                .createdAt(Instant.now())
                .build();

        User userB = User.builder()
                .id(USER_B_ID)
                .email("userB@example.com")
                .fullName("User B")
                .passwordHash(encoder.encode("Password@123"))
                .role(Role.USER)
                .verified(false)
                .balance(new BigDecimal("200000"))
                .createdAt(Instant.now())
                .build();

        User admin = User.builder()
                .id(ADMIN_ID)
                .email("admin@example.com")
                .fullName("Admin")
                .passwordHash(encoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .verified(true)
                .balance(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(admin);

        orderRepository.save(Order.builder()
                .id(ORDER_A_ID)
                .user(userA)
                .totalAmount(new BigDecimal("150000"))
                .status(OrderStatus.PAID)
                .createdAt(Instant.now())
                .build());

        orderRepository.save(Order.builder()
                .id(ORDER_B_ID)
                .user(userB)
                .totalAmount(new BigDecimal("250000"))
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .build());
    }
}
