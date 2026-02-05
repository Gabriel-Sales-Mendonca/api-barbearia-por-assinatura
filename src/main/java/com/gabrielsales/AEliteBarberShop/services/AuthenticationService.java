package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.UserRole;
import com.gabrielsales.AEliteBarberShop.repositories.PendingUserRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PendingUserRepository pendingUserRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByLogin(username);
    }

    public void register(PendingUser pendingUser) {
        String verificationCode = PendingUser.generateVerificationCode();

        String passwordEncoded = new BCryptPasswordEncoder().encode(pendingUser.getPassword());

        pendingUser.setPassword(passwordEncoded);
        pendingUser.setVerificationCode(verificationCode);
        pendingUser.setCreatedAt(LocalDateTime.now());
        pendingUser.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        this.pendingUserRepository.save(pendingUser);
        log.info("Usuário pendente criado com sucesso");

        this.emailService.sendVerificationCodeEmail(pendingUser.getLogin(), pendingUser.getName(), verificationCode);
    }

    public void verifyEmail(String email, String verificationCode) {
        PendingUser pendingUser = this.pendingUserRepository.findByLogin(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));

        if (!pendingUser.getVerificationCode().equals(verificationCode)) throw new InvalidResourceException("Código de verificação inválido");
        if (pendingUser.isExpired()) throw new CredentialsExpiredException("Código de verificação expirado, solicite um novo!");

        log.info("Código de verificação de email verificado com sucesso");

        User user = new User(
                pendingUser.getLogin(),
                pendingUser.getPassword(),
                pendingUser.getName(),
                pendingUser.getLastname(),
                UserRole.USER
        );

        this.userRepository.save(user);
        this.pendingUserRepository.deleteById(pendingUser.getId());

        log.info("Novo usuário criado com sucesso");
    }

    public void resendCode(String email) {
        PendingUser pendingUser = this.pendingUserRepository.findByLogin(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));

        String verificationCode = PendingUser.generateVerificationCode();

        pendingUser.setVerificationCode(verificationCode);
        pendingUser.setCreatedAt(LocalDateTime.now());
        pendingUser.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        this.pendingUserRepository.save(pendingUser);

        this.emailService.sendVerificationCodeEmail(pendingUser.getLogin(), pendingUser.getName(), pendingUser.getVerificationCode());
        log.info("Código de verificação de email enviado novamente para: {}", pendingUser.getLogin());
    }

}
