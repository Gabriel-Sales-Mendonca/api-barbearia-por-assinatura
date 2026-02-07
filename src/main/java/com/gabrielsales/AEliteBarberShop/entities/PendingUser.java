package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "tb_pending_user")
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String name;
    private String lastname;
    private String verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    public PendingUser() {};

    public PendingUser(String login, String password, String name, String lastname) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
    }

    public PendingUser(String login, String password, String name, String lastname, String verificationCode, LocalDateTime createdAt, LocalDateTime expiryDate) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
    }

    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
