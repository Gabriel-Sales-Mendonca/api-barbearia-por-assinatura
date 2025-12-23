package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.OrderRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.OrderResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDTO orderRequestDTO) {
        return new Order(
                orderRequestDTO.value(),
                orderRequestDTO.date()
        );
    }

    public OrderResponseDTO toDTO(Order order) {
        return new OrderResponseDTO(
                order.getValue(),
                order.getDate(),
                order.getOrderStatus().getOrderStatus(),
                order.getPlan().getId()
        );
    }

}
