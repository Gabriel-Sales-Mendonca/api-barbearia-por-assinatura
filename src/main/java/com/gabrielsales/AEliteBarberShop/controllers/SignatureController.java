package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.SignatureResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Signature;
import com.gabrielsales.AEliteBarberShop.mappers.SignatureMapper;
import com.gabrielsales.AEliteBarberShop.services.SignatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signatures")
public class SignatureController {

    private final SignatureService signatureService;
    private final SignatureMapper signatureMapper;

    public SignatureController(SignatureService signatureService, SignatureMapper signatureMapper) {
        this.signatureService = signatureService;
        this.signatureMapper = signatureMapper;
    }

    @GetMapping("/my")
    public ResponseEntity<SignatureResponseDTO> findByUserId() {
        Signature signature = this.signatureService.findByUserId();
        SignatureResponseDTO signatureResponseDTO = this.signatureMapper.toDTO(signature);

        return ResponseEntity.status(HttpStatus.OK).body(signatureResponseDTO);
    }

}
