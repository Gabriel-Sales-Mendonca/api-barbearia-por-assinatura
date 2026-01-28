package com.gabrielsales.AEliteBarberShop.dtos;

import java.time.LocalDate;

public record OrderToApproveResponseDTO(
        Long id,
        Double value,
        LocalDate date,
        String proofOfPaymentSecureUrl,
        String orderUserLogin,
        String orderPlanName
) {
}
