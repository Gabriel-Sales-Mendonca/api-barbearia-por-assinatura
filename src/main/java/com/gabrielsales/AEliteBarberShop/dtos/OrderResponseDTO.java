package com.gabrielsales.AEliteBarberShop.dtos;

import java.time.LocalDate;

public record OrderResponseDTO(
        Double value,
        LocalDate date,
        String orderStatus,
        Long planId
) {
}
