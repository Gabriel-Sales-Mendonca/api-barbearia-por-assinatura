package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.PlanRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.PlanResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.mappers.PlanMapper;
import com.gabrielsales.AEliteBarberShop.services.PlanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<PlanResponseDTO> create(@RequestBody @Valid PlanRequestDTO planRequestDTO) {
        Plan newPlan = this.planMapper.toEntity(planRequestDTO);
        Plan planCreated = this.planService.create(newPlan);
        PlanResponseDTO planResponseDTO = this.planMapper.toDTO(planCreated);

        return ResponseEntity.status(HttpStatus.CREATED).body(planResponseDTO);
    }

    @GetMapping()
    public ResponseEntity<Page<PlanResponseDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        Page<Plan> plans = this.planService.findAll(pageable);

        Page<PlanResponseDTO> plansDTO = plans
                .map(plan -> this.planMapper.toDTO(plan));

        return ResponseEntity.status(HttpStatus.OK).body(plansDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> findById(@PathVariable Long id) {
        Plan plan = this.planService.findById(id);
        PlanResponseDTO planResponseDTO = this.planMapper.toDTO(plan);

        return ResponseEntity.status(HttpStatus.OK).body(planResponseDTO);
    }
}
