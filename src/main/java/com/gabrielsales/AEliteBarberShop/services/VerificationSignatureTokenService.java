package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.VerificationSignatureToken;
import com.gabrielsales.AEliteBarberShop.repositories.VerificationSignatureTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class VerificationSignatureTokenService {

    private final VerificationSignatureTokenRepository verificationSignatureTokenRepository;
    private final UserService userService;

    public VerificationSignatureTokenService(VerificationSignatureTokenRepository verificationSignatureTokenRepository, UserService userService) {
        this.verificationSignatureTokenRepository = verificationSignatureTokenRepository;
        this.userService = userService;
    }

    public String generateVerificationToken() {
        User user = this.userService.getTokenUser();

        String verificationToken = this.generateToken();

        VerificationSignatureToken verificationSignatureToken = new VerificationSignatureToken(
                user.getId(),
                verificationToken,
                LocalDateTime.now().plusMinutes(5)
        );

        this.verificationSignatureTokenRepository.save(verificationSignatureToken);

        return verificationToken;
    }

    @Transactional
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void deleteExpiredTokens() {
        this.verificationSignatureTokenRepository.deleteAllByExpireAtBefore(LocalDateTime.now());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

}
