package com.gabrielsales.AEliteBarberShop.repositories;

import com.gabrielsales.AEliteBarberShop.entities.PasswordForgot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordForgotRepository extends JpaRepository<PasswordForgot, Long> {
    Optional<PasswordForgot> findByEmail(String email);
}
