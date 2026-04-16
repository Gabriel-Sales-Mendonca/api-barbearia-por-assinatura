package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.services.VerificationSignatureTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signature-token")
public class VerificationSignatureTokenController {

    private final VerificationSignatureTokenService verificationSignatureTokenService;

    public VerificationSignatureTokenController(VerificationSignatureTokenService verificationSignatureTokenService) {
        this.verificationSignatureTokenService = verificationSignatureTokenService;
    }

    @PostMapping
    public ResponseEntity<String> generateVerificationToken() {
        String sigatureToken = this.verificationSignatureTokenService.generateVerificationToken();

        return ResponseEntity.status(HttpStatus.OK).body(sigatureToken);
    }

}
