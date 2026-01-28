package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.OrderResponseDTO;
import com.gabrielsales.AEliteBarberShop.dtos.OrderToApproveResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDTO toDTO(Order order) {
        return new OrderResponseDTO(
                order.getValue(),
                order.getDate(),
                order.getOrderStatus().getOrderStatus(),
                order.getPlan().getId()
        );
    }

    public OrderToApproveResponseDTO toOrderToApprove(Order order) {
        return new OrderToApproveResponseDTO(
                order.getId(),
                order.getValue(),
                order.getDate(),
                order.getProofOfPaymentSecureUrl(),
                order.getUser().getLogin(),
                order.getPlan().getName()
        );
    }

}
