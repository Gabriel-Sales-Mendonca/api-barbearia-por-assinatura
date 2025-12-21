package com.gabrielsales.AEliteBarberShop.dtos;

public record UserUpdatePasswordDTO(
        String oldPassword,
        String newPassword
) {
}
