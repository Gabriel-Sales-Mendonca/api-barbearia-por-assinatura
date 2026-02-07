package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_password_forgot")
public class PasswordForgot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String verificationCode;
    private LocalDateTime expiryDate;
    private Integer attemptsRecoveryAccess = 0;
    private Integer attemptsPasswordForgot = 0;

    public PasswordForgot() {
    }

    public PasswordForgot(String email, String verificationCode, LocalDateTime expiryDate) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getAttemptsRecoveryAccess() {
        return attemptsRecoveryAccess;
    }

    public void setAttemptsRecoveryAccess(Integer attemptsRecoveryAccess) {
        this.attemptsRecoveryAccess = attemptsRecoveryAccess;
    }

    public Integer getAttemptsPasswordForgot() {
        return attemptsPasswordForgot;
    }

    public void setAttemptsPasswordForgot(Integer attemptsPasswordForgot) {
        this.attemptsPasswordForgot = attemptsPasswordForgot;
    }
}
