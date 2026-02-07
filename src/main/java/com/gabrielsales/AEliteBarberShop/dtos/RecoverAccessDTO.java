package com.gabrielsales.AEliteBarberShop.dtos;

public record RecoverAccessDTO(
        String email,
        String verificationCode,
        String password
) {
}
