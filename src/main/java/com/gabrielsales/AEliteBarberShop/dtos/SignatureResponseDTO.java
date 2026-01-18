package com.gabrielsales.AEliteBarberShop.dtos;

import java.time.LocalDate;

public record SignatureResponseDTO(
        LocalDate acquisitionDate,
        LocalDate expirationDate,
        PlanResponseDTO planResponseDTO
) {
}
