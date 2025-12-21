package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.UserUpdateDTO;
import com.gabrielsales.AEliteBarberShop.dtos.UserUpdatePasswordDTO;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public User getTokenUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return (User) user;
    }

    public User update(UserUpdateDTO data) {
        User user = this.getTokenUser();

        user.setName(data.name());
        user.setLastname(data.lastname());

        return this.userRepository.save(user);
    }

    public void updatePassword(UserUpdatePasswordDTO data) {
        User user = this.getTokenUser();

        if (!new BCryptPasswordEncoder().matches(data.oldPassword(), user.getPassword())) {
            throw new ValidationException("Senha antiga incorreta");
        }

        String passwordEncoded = new BCryptPasswordEncoder().encode(data.newPassword());
        user.setPassword(passwordEncoded);

        this.userRepository.save(user);
    }
}
