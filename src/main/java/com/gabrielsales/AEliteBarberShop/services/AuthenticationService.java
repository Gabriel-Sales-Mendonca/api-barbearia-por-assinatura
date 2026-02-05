package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.repositories.PendingUserRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;

    public AuthenticationService(UserRepository userRepository, PendingUserRepository pendingUserRepository) {
        this.userRepository = userRepository;
        this.pendingUserRepository = pendingUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByLogin(username);
    }

    public void verifyEmail(String email, String verificationCode) {
        PendingUser pendingUser = this.pendingUserRepository.findByLogin(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));

        if (!pendingUser.getVerificationCode().equals(verificationCode)) throw new InvalidResourceException("Código de verificação inválido");
        if (pendingUser.isExpired()) throw new CredentialsExpiredException("Código de verificação expirado, solicite um novo!");

        log.info("Código de verificação de email verificado com sucesso");
    }
}
