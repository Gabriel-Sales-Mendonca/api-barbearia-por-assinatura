package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.PlanRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.PlanResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.mappers.PlanMapper;
import com.gabrielsales.AEliteBarberShop.services.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;
    private final PlanMapper planMapper;

    public PlanController(PlanService planService, PlanMapper planMapper) {
        this.planService = planService;
        this.planMapper = planMapper;
    }

    @PostMapping
    public ResponseEntity<PlanResponseDTO> create(@RequestBody PlanRequestDTO planRequestDTO) {
        Plan newPlan = this.planMapper.toEntity(planRequestDTO);
        Plan planCreated = this.planService.create(newPlan);
        PlanResponseDTO planResponseDTO = this.planMapper.toDTO(planCreated);

        return ResponseEntity.status(HttpStatus.CREATED).body(planResponseDTO);
    }
}
