package com.gabrielsales.AEliteBarberShop.repositories;

import com.gabrielsales.AEliteBarberShop.entities.VerificationSignatureToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VerificationSignatureTokenRepository extends JpaRepository<VerificationSignatureToken, String> {
    void deleteAllByExpireAtBefore(LocalDateTime now);
    void deleteByUserId(Long userId);
}
