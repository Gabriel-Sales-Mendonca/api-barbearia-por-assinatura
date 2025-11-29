package com.gabrielsales.AEliteBarberShop.dtos;

public record RegisterDTO(
        String name,
        String lastname,
        String login,
        String password
) {}
