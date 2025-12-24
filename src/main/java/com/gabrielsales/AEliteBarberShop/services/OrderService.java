package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Order;
import com.gabrielsales.AEliteBarberShop.entities.OrderStatus;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PlanService planService;

    public OrderService(OrderRepository orderRepository, UserService userService, PlanService planService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.planService = planService;
    }

    public Order create(Long planId) {
        User user = this.userService.getTokenUser();
        Plan plan = this.planService.findById(planId);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PROOF_OF_PAYMENT,
                user,
                plan
        );

        return this.orderRepository.save(order);
    }
}
