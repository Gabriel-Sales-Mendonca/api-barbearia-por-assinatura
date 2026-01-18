package com.gabrielsales.AEliteBarberShop.repositories;

import com.gabrielsales.AEliteBarberShop.entities.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
    Optional<Signature> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
