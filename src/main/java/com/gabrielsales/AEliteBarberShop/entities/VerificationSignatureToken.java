package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_verification_signature_token")
public class VerificationSignatureToken {

    @Id
    private String verificationToken;
    private Long userId;
    private LocalDateTime expireAt;

    public VerificationSignatureToken() {
    }

    public VerificationSignatureToken(String verificationToken, Long userId, LocalDateTime expireAt) {
        this.verificationToken = verificationToken;
        this.userId = userId;
        this.expireAt = expireAt;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
