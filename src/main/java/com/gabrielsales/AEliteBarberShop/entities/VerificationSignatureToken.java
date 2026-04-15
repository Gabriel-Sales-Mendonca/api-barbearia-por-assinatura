package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_verification_signature_token")
public class VerificationSignatureToken {

    @Id
    private Long userId;
    private String verificationToken;
    private LocalDateTime expireAt;

    public VerificationSignatureToken() {
    }

    public VerificationSignatureToken(Long userId, String verificationToken, LocalDateTime expireAt) {
        this.userId = userId;
        this.verificationToken = verificationToken;
        this.expireAt = expireAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
