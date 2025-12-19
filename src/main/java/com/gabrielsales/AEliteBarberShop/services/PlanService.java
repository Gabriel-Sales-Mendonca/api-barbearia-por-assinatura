package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.repositories.PlanRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan create(Plan plan) {
        Plan searchedPlan = this.findByName(plan.getName());
        if (searchedPlan != null) throw new ResourceAlreadyExistsException("Já existe um plano com esse nome.");

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

    public Plan update(Long id, Plan plan) {
        Plan planDB = this.findById(id);

        Plan searchedPlan = this.findByName(plan.getName());
        if (searchedPlan != null) throw new ResourceAlreadyExistsException("Já existe um plano com esse nome.");

        planDB.setName(plan.getName());
        planDB.setDescription(plan.getDescription());
        planDB.setPrice(plan.getPrice());

        return this.planRepository.save(planDB);
    }

    public void delete(Long id) {
        Plan planDB = this.findById(id);
        this.planRepository.deleteById(id);
    }

    public Plan findByName(String name) {
        return this.planRepository.findByName(name).orElse(null);
    }
}
