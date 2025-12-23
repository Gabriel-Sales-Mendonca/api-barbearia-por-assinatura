package com.gabrielsales.AEliteBarberShop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não informado")
    @Size(max = 40, message = "O nome para o Plano é muito grande")
    private String name;

    @NotBlank(message = "Descrição não informada")
    private String description;

    @NotNull(message = "Preço não informado")
    @Positive(message = "O preço não pode ser abaixo de 0")
    private Double price;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Plan() {}

    public Plan(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
