package com.gabrielsales.AEliteBarberShop.mappers;

import com.gabrielsales.AEliteBarberShop.dtos.UserResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getLastname(),
                user.getRole(),
                user.getDesiredPlan(),
                user.getProofOfPayments()
        );
    }

}
