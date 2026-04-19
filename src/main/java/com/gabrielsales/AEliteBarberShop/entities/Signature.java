package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_signature")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate acquisitionDate;
    private LocalDate expirationDate;
    private String userName;
    private String userLastName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    public Signature() {
    }

    public Signature(LocalDate acquisitionDate, LocalDate expirationDate, User user, Plan plan) {
        this.acquisitionDate = acquisitionDate;
        this.expirationDate = expirationDate;
        this.user = user;
        this.plan = plan;
        if (user != null) {
            this.userName = user.getName();
            this.userLastName = user.getLastname();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
}
