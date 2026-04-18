package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.VerificationSignatureToken;
import com.gabrielsales.AEliteBarberShop.repositories.VerificationSignatureTokenRepository;
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

    public VerificationSignatureTokenService(VerificationSignatureTokenRepository verificationSignatureTokenRepository, UserService userService) {
        this.verificationSignatureTokenRepository = verificationSignatureTokenRepository;
        this.userService = userService;
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
