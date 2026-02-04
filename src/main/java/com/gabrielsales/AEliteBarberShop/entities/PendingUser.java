package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    private String verificationToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    public PendingUser() {};

    public PendingUser(String login, String password, String name, String lastname, String verificationToken, LocalDateTime createdAt, LocalDateTime expiryDate) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.verificationToken = verificationToken;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
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

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
