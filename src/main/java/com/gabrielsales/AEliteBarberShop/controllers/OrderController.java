package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.OrderRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.OrderResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Order;
import com.gabrielsales.AEliteBarberShop.mappers.OrderMapper;
import com.gabrielsales.AEliteBarberShop.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = this.orderMapper.toEntity(orderRequestDTO);
        Order createdOrder = this.orderService.create(order, orderRequestDTO.planId());
        OrderResponseDTO orderReponseDTO = this.orderMapper.toDTO(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderReponseDTO);
    }
}
