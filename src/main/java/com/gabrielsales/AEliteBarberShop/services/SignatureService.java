package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.entities.Signature;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.SignatureRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final UserService userService;

    public SignatureService(SignatureRepository signatureRepository, UserService userService) {
        this.signatureRepository = signatureRepository;
        this.userService = userService;
    }

    protected void create(LocalDate acquisitionDate, Plan plan, User user) {
        LocalDate expirationDate = acquisitionDate.plusDays(30);

        Signature existingSignature = this.signatureRepository.findByUserId(user.getId())
                .orElse(null);

        if (existingSignature != null) {
            existingSignature.setAcquisitionDate(acquisitionDate);
            existingSignature.setExpirationDate(expirationDate);
            existingSignature.setPlan(plan);

            this.signatureRepository.save(existingSignature);
            return;
        }

        Signature signature = new Signature(acquisitionDate, expirationDate, user, plan);
        this.signatureRepository.save(signature);
    }

    public Signature findByUserId() {
        User user = this.userService.getTokenUser();

        return this.signatureRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(user.getId()));
    }

}
