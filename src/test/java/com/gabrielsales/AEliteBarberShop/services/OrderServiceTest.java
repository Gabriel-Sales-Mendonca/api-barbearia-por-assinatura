package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.*;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private Plan plan;

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
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        tokenUser.setId(1L);

        BDDMockito.given(this.userService.getTokenUser()).willReturn(tokenUser);
    }

    @Test
    @DisplayName("Should throw exception when already exist order with status AWAITING_PROOF_OF_PAYMENT")
    void create_ShouldThrowException_WhenAlreadyExistOrderWithStatusAWAITING_PROOF_OF_PAYMENT() {
        plan.setPrice(1.0);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PROOF_OF_PAYMENT,
                orderUser,
                plan
        );

        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Should throw exception when already exist order with status AWAITING_PAYMENT_APPROVAL")
    void create_ShouldThrowException_WhenAlreadyExistOrderWithStatusAWAITING_PAYMENT_APPROVAL() {
        plan.setPrice(1.0);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PAYMENT_APPROVAL,
                orderUser,
                plan
        );

        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status PAYMENT_APPROVED")
    void create_DoesNotShouldThrowException_WhenAlreadyExistOrderWithStatusPAYMENT_APPROVED() {
        plan.setPrice(1.0);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.PAYMENT_APPROVED,
                orderUser,
                plan
        );

        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status PAYMENT_REJECTED")
    void create_DoesNotShouldThrowException_WhenAlreadyExistOrderWithStatusPAYMENT_REJECTED() {
        plan.setPrice(1.0);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.PAYMENT_REJECTED,
                orderUser,
                plan
        );

        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Doesn't should throw exception when already exist order with status CANCELED_ORDER")
    void create_DoesNotShouldThrowException_WhenAlreadyExistOrderWithStatusCANCELED_ORDER() {
        plan.setPrice(1.0);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.CANCELED_ORDER,
                orderUser,
                plan
        );

        BDDMockito.given(this.planService.findById(anyLong())).willReturn(plan);
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(order);

        Assertions.assertDoesNotThrow(() -> this.orderService.create(1L));
    }

    @Test
    @DisplayName("Should throw exception when the order doesn't exist")
    void findById_ShouldThrowException_WhenOrderDoesNotExist() {
        BDDMockito.given(this.orderRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.orderService.findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when the order doesn't exist for the token user")
    void findById_ShouldThrowException_WhenOrderDoesNotExistForTheTokenUser() {
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        tokenUser.setId(1L);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.CANCELED_ORDER,
                orderUser,
                plan
        );

        BDDMockito.given(this.userService.getTokenUser()).willReturn(tokenUser);
        BDDMockito.given(this.orderRepository.findById(any())).willReturn(Optional.of(order));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.orderService.findById(1L));
    }

    @Test
    @DisplayName("Return Order when the order exist for the token user")
    void findById_ReturnOrder_WhenOrderExistForTheTokenUser() {
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        tokenUser.setId(2L);

        User orderUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.CANCELED_ORDER,
                orderUser,
                plan
        );

        BDDMockito.given(this.userService.getTokenUser()).willReturn(tokenUser);
        BDDMockito.given(this.orderRepository.findById(any())).willReturn(Optional.of(order));

        Assertions.assertEquals(order, this.orderService.findById(1L));
    }

    @Test
    @DisplayName("Return Order when the order exist and the user is ADMIN")
    void findById_ReturnOrder_WhenOrderExistAndTheUserIsADMIN() {
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.ADMIN
        );
        tokenUser.setId(1L);

        User orderUser = new User(
                "admin@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        orderUser.setId(2L);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.CANCELED_ORDER,
                orderUser,
                plan
        );

        BDDMockito.given(this.userService.getTokenUser()).willReturn(tokenUser);
        BDDMockito.given(this.orderRepository.findById(any())).willReturn(Optional.of(order));

        Assertions.assertEquals(order, this.orderService.findById(1L));
    }

    @Test
    @DisplayName("Should cancel order when exists order AWAITING_PROOF_OF_PAYMENT")
    void cancel_ShouldCancelOrder_WhenExistsOrderAWAITING_PROOF_OF_PAYMENT() {
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        tokenUser.setId(1L);

        Order existingOrder = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PROOF_OF_PAYMENT,
                tokenUser,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(existingOrder);
        BDDMockito.given(this.orderRepository.save(any())).willReturn(existingOrder);

        Order order = this.orderService.cancel();

        Assertions.assertEquals(OrderStatus.CANCELED_ORDER, order.getOrderStatus());
    }

    @Test
    @DisplayName("Should cancel order when exists order AWAITING_PROOF_OF_PAYMENT")
    void cancel_ShouldCancelOrder_WhenExistsOrderAWAITING_PAYMENT_APPROVAL() {
        User tokenUser = new User(
                "example@email.com",
                "password",
                "Name",
                "Lastname",
                UserRole.USER
        );
        tokenUser.setId(1L);

        Order existingOrder = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PAYMENT_APPROVAL,
                tokenUser,
                plan
        );

        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(existingOrder);
        BDDMockito.given(this.orderRepository.save(any())).willReturn(existingOrder);

        Order order = this.orderService.cancel();

        Assertions.assertEquals(OrderStatus.CANCELED_ORDER, order.getOrderStatus());
    }

    @Test
    @DisplayName("Does not should cancel order when not exists order AWAITING_PROOF_OF_PAYMENT or AWAITING_PAYMENT_APPROVAL")
    void cancel_DoesNotShouldCancelOrder_WhenNotExistsOrderAWAITING_PROOF_OF_PAYMENT_Or_AWAITING_PAYMENT_APPROVAL() {
        BDDMockito.given(this.orderRepository.findAllByUserIdAndOrderStatusIn(any(), any())).willReturn(null);

        Assertions.assertThrows(InvalidResourceException.class, () -> this.orderService.cancel());
    }

}