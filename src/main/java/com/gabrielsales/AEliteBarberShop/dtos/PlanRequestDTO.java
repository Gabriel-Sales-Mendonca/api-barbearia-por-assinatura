package com.gabrielsales.AEliteBarberShop.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlanRequestDTO(
        @NotBlank(message = "Campo 'name' não pode ser vazio")
        @Size(min = 1, max = 50, message = "Campo 'name' deve ter entre 1 e 50 caracteres")
        String name,

        @NotBlank(message = "Campo 'description' não pode ser vazio")
        @Size(min = 1, max = 200, message = "Campo 'description' deve ter entre 1 e 200 caracteres")
        String description,

        @Min(value = 0, message = "campo 'price' não pode ser menor que zero")
        Double price
) {}
