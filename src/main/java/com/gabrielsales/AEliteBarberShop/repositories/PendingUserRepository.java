package com.gabrielsales.AEliteBarberShop.repositories;

import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByLogin(String login);

    boolean existsByLogin(String login);
}
