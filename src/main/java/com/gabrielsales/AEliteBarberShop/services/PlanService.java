package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.repositories.PlanRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan create(Plan plan) {
        return this.planRepository.save(plan);
    }

    public Page<Plan> findAll(Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    50,
                    pageable.getSort()
            );
        }

        return this.planRepository.findAll(pageable);
    }

    public Plan findById(Long id) {
        return this.planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
