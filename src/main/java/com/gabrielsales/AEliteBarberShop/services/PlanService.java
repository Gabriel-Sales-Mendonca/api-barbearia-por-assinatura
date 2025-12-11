package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.repositories.PlanRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan create(Plan plan) {
        if (plan.getName().equals("teste")) throw new RuntimeException("Erro de teste");
        return this.planRepository.save(plan);
    }

}
