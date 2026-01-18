package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.SignatureResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Signature;
import org.springframework.stereotype.Component;

@Component
public class SignatureMapper {

    public SignatureResponseDTO toDTO(Signature signature) {
        return new SignatureResponseDTO(
                signature.getAcquisitionDate(),
                signature.getExpirationDate(),
                new PlanMapper().toDTO(signature.getPlan())
        );
    }

}
