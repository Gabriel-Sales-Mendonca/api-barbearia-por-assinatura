package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.PlanRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.PlanResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public Plan toEntity(PlanRequestDTO planRequestDTO) {
        return new Plan(
                planRequestDTO.name(),
                planRequestDTO.description(),
                planRequestDTO.price()
        );
    }

    public PlanResponseDTO toDTO(Plan plan) {
        return new PlanResponseDTO(
                plan.getName(),
                plan.getDescription(),
                plan.getPrice()
        );
    }
}
