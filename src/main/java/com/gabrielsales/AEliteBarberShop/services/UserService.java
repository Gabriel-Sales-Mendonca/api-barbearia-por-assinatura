package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.UserUpdateDTO;
import com.gabrielsales.AEliteBarberShop.dtos.UserUpdatePasswordDTO;
import com.gabrielsales.AEliteBarberShop.entities.PasswordForgot;
import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.PasswordForgotRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordForgotRepository passwordForgotRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordForgotRepository passwordForgotRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordForgotRepository = passwordForgotRepository;
        this.emailService = emailService;
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

    public void passwordForgot(String email) {
        User user = (User) this.userRepository.findByLogin(email);
        if (user == null) throw new ResourceNotFoundException(email);

        String verificationCode = PendingUser.generateVerificationCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordForgot passwordForgotDB = this.passwordForgotRepository.findByEmail(email)
                .orElse(null);

        if (passwordForgotDB == null) {
            PasswordForgot newPasswordForgot = new PasswordForgot(email, verificationCode, expiryDate);
            this.passwordForgotRepository.save(newPasswordForgot);
        } else {
            passwordForgotDB.setVerificationCode(verificationCode);
            passwordForgotDB.setExpiryDate(expiryDate);
            this.passwordForgotRepository.save(passwordForgotDB);
        }

        this.emailService.sendAccessRecoveryEmail(user.getLogin(), user.getName(), verificationCode);
    }
}
