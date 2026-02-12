package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Order;
import com.gabrielsales.AEliteBarberShop.entities.OrderStatus;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private User user;

    @Mock
    private Plan plan;

    @Mock
    private Order order;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private PlanService planService;

    @Mock
    private SignatureService signatureService;

    @BeforeEach
    void setUp() {
        user.setId(1L);
        plan.setPrice(1.0);

        BDDMockito.given(this.userService.getTokenUser()).willReturn(user);
        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
    }

    @Test
    @DisplayName("Should throw exception when already exist order with status AWAITING_PROOF_OF_PAYMENT")
    void create_ShouldThrowException_WHenAlreadyExistOrderWithStatusAWAITING_PROOF_OF_PAYMENT() {
        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PROOF_OF_PAYMENT,
                user,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Should throw exception when already exist order with status AWAITING_PAYMENT_APPROVAL")
    void create_ShouldThrowException_WHenAlreadyExistOrderWithStatusAWAITING_PAYMENT_APPROVAL() {
        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PAYMENT_APPROVAL,
                user,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status PAYMENT_APPROVED")
    void create_DoesNotShouldThrowException_WHenAlreadyExistOrderWithStatusPAYMENT_APPROVED() {
        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.PAYMENT_APPROVED,
                user,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status PAYMENT_REJECTED")
    void create_DoesNotShouldThrowException_WHenAlreadyExistOrderWithStatusPAYMENT_REJECTED() {
        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.PAYMENT_REJECTED,
                user,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status CANCELED_ORDER")
    void create_DoesNotShouldThrowException_WHenAlreadyExistOrderWithStatusCANCELED_ORDER() {
        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.CANCELED_ORDER,
                user,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

}