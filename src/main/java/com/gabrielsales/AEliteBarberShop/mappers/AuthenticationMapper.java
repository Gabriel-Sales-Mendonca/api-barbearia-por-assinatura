package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.RegisterDTO;
import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthenticationMapper {

    public PendingUser toEntity(RegisterDTO data) {
        return new PendingUser(
                data.login(),
                data.password(),
                data.name(),
                data.lastname()
        );
    }

}
