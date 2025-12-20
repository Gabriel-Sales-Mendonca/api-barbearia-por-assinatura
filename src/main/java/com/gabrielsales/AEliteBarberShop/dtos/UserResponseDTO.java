package com.gabrielsales.AEliteBarberShop.dtos;

import com.gabrielsales.AEliteBarberShop.entities.UserRole;

import java.util.List;

public record UserResponseDTO(
        Long id,
        String login,
        String name,
        String lastname,
        UserRole role,
        Integer desiredPlan,
        List<String>proofOfPayments
) {}
