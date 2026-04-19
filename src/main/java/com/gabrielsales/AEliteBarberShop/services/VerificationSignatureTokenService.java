package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.PlanResponseDTO;
import com.gabrielsales.AEliteBarberShop.dtos.SignatureResponseDTO;
import com.gabrielsales.AEliteBarberShop.dtos.ValidateVerificationSignatureTokenResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.Signature;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.VerificationSignatureToken;
import com.gabrielsales.AEliteBarberShop.repositories.SignatureRepository;
import com.gabrielsales.AEliteBarberShop.repositories.VerificationSignatureTokenRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidOrExpiredTokenException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class VerificationSignatureTokenService {

    private static final Logger log = LoggerFactory.getLogger(VerificationSignatureTokenService.class);
    private final VerificationSignatureTokenRepository verificationSignatureTokenRepository;
    private final UserService userService;
    private final SignatureRepository signatureRepository;

    public VerificationSignatureTokenService(VerificationSignatureTokenRepository verificationSignatureTokenRepository, UserService userService, SignatureRepository signatureRepository) {
        this.verificationSignatureTokenRepository = verificationSignatureTokenRepository;
        this.userService = userService;
        this.signatureRepository = signatureRepository;
    }

    @Transactional
    public String generateVerificationToken() {
        User user = this.userService.getTokenUser();

        this.verificationSignatureTokenRepository.deleteByUserId(user.getId());

        log.info("Gerando token de verificação da assinatura para o usuário: {}", user.getId());
        String verificationToken = this.generateToken();

        VerificationSignatureToken verificationSignatureToken = new VerificationSignatureToken(
                verificationToken,
                user.getId(),
                LocalDateTime.now().plusMinutes(5)
        );

        this.verificationSignatureTokenRepository.save(verificationSignatureToken);
        log.info("Token de verificação de assinatura salvo com sucesso para o usuário: {}", user.getId());

        return verificationToken;
    }

    @Transactional
    public ValidateVerificationSignatureTokenResponseDTO validateVerificationToken(String token) {
        log.info("Iniciando processo de validação do token de verificação de assinatura");

        VerificationSignatureToken verificationToken = this.verificationSignatureTokenRepository.findById(token)
                .orElseThrow(() -> new InvalidOrExpiredTokenException("Token inválido ou expirado"));

        if (verificationToken.getExpireAt().isBefore(LocalDateTime.now())) {
            this.verificationSignatureTokenRepository.delete(verificationToken);
            log.warn("Token de verificação de assinatura foi encontrado mas estava expirado");
            throw new InvalidOrExpiredTokenException("Token inválido ou expirado");
        }

        Signature signature = this.signatureRepository.findByUserId(verificationToken.getUserId())
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada para este usuário"));

        this.verificationSignatureTokenRepository.delete(verificationToken);
        log.info("Token de verificação de assinatura do usuário {} {} é válido e foi deletado", signature.getUserName(), signature.getUserLastName());

        PlanResponseDTO planDTO = new PlanResponseDTO(
                signature.getPlan().getId(),
                signature.getPlan().getName(),
                signature.getPlan().getDescription(),
                signature.getPlan().getPrice()
        );

        SignatureResponseDTO signatureDTO = new SignatureResponseDTO(
                signature.getAcquisitionDate(),
                signature.getExpirationDate(),
                planDTO
        );

        return new ValidateVerificationSignatureTokenResponseDTO(
                signature.getUserName(),
                signature.getUserLastName(),
                signatureDTO
        );
    }

    @Transactional
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void deleteExpiredTokens() {
        this.verificationSignatureTokenRepository.deleteAllByExpireAtBefore(LocalDateTime.now());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

}
