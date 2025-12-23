package com.gabrielsales.AEliteBarberShop.dtos;

import java.time.LocalDate;

public record OrderRequestDTO(
        Double value,
        LocalDate date,
        Long planId
) {
}
