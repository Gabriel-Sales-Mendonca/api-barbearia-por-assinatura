package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    private String role;
    private Integer desiredPlan;
    private List<String> proofOfPayments = new ArrayList<>();

    public User(String name, String lastname, String role) {
        Long id = this.generateId();
        setId(id);

        this.name = name;
        this.lastname = lastname;
        this.role = role;
    }

    private Long generateId() {
        Random random = new Random();

        int number = random.nextInt(100);
        return (long) number;
    }

    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public Integer getDesiredPlan() {
        return desiredPlan;
    }

    public void setDesiredPlan(Integer desiredPlan) {
        this.desiredPlan = desiredPlan;
    }

    public List<String> getProofOfPayments() {
        return proofOfPayments;
    }

    public void setProofOfPayments(List<String> proofOfPayments) {
        this.proofOfPayments = proofOfPayments;
    }

}
