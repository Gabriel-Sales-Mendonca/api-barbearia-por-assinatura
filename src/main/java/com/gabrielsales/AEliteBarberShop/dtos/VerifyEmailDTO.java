package com.gabrielsales.AEliteBarberShop.dtos;

public record VerifyEmailDTO(
        String email,
        String verificationCode
) {
}
