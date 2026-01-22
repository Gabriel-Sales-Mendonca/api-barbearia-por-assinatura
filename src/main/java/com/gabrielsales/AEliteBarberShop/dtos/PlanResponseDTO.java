package com.gabrielsales.AEliteBarberShop.dtos;

public record PlanResponseDTO(
        Long id,
        String name,
        String description,
        Double price
) {}
