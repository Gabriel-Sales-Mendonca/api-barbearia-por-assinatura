package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.repositories.PlanRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @Test
    @DisplayName("Should update fields when the name does not exist in another plan")
    void update_ShouldUpdateFields_WhenTheNameDoesNotExistInAnotherPlan() {
        Long id = 1L;
        Plan planForUpdate =  new Plan("Nome diferente", "Descrição diferente", 15.0);
        Plan planDB =  new Plan("Nome", "Descrição", 10.0);
        ReflectionTestUtils.setField(planDB, "id", id);

        BDDMockito.when(planRepository.findById(any())).thenReturn(Optional.of(planDB));
        BDDMockito.when(planRepository.findByName(any())).thenReturn(Optional.empty());
        BDDMockito.when(planRepository.save(any())).thenReturn(planDB);

        planService.update(id, planForUpdate);

        Assertions.assertEquals(planForUpdate.getName(), planDB.getName());
        Assertions.assertEquals(planForUpdate.getDescription(), planDB.getDescription());
        Assertions.assertEquals(planForUpdate.getPrice(), planDB.getPrice());
    }

    @Test
    @DisplayName("Should not update the name when the name already exist in another plan")
    void update_ShouldNotUpdateTheName_WhenTheNameAlreadyExistInAnotherPlan() {
        Long id = 1L;
        Plan planForUpdate =  new Plan("Nome diferente", "Descrição", 10.0);
        Plan planDB =  new Plan("Nome", "Descrição", 10.0);
        ReflectionTestUtils.setField(planDB, "id", id);

        Plan differentPlan = new Plan("Nome diferente", "Descrição", 10.0);
        ReflectionTestUtils.setField(differentPlan, "id", 2L);

        BDDMockito.when(planRepository.findById(any())).thenReturn(Optional.of(planDB));
        BDDMockito.when(planRepository.findByName(any())).thenReturn(Optional.of(differentPlan));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> planService.update(id, planForUpdate));
    }

}