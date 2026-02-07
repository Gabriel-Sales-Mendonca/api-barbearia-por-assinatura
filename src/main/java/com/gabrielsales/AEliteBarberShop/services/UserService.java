package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.UserUpdateDTO;
import com.gabrielsales.AEliteBarberShop.dtos.UserUpdatePasswordDTO;
import com.gabrielsales.AEliteBarberShop.entities.PasswordForgot;
import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.PasswordForgotRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.LimitExceededException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
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
            passwordForgotDB.setAttemptsRecoveryAccess(0);
            passwordForgotDB.setAttemptsPasswordForgot(passwordForgotDB.getAttemptsPasswordForgot() + 1);
            this.passwordForgotRepository.save(passwordForgotDB);

            if (passwordForgotDB.getAttemptsPasswordForgot() > 10) throw new LimitExceededException("Você atingiu o limite de tentativas de recuperação de senha, entre em contato com a barbearia.");
        }

        this.emailService.sendAccessRecoveryEmail(user.getLogin(), user.getName(), verificationCode);
    }

    public void recoverAccess(String email, String verificationCode, String password) {
        PasswordForgot passwordForgot = this.passwordForgotRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));

        User user = (User) this.userRepository.findByLogin(email);
        if (user == null) {
            log.warn("Usuário não existe na tabela de usuário, mas existe na tabela de esqueceu a senha");
            throw new InvalidResourceException("Usuário não existe");
        }

        passwordForgot.setAttemptsRecoveryAccess(passwordForgot.getAttemptsRecoveryAccess() + 1);
        if (passwordForgot.getAttemptsRecoveryAccess() > 5) {
            log.warn("Usuário: {} atingiu o limite de tentativas de recuperar a senha para um determinado código", email);
            throw new LimitExceededException("Você atingiu o limite de tentativas, solicite um novo código.");
        }

        this.passwordForgotRepository.save(passwordForgot);

        if (!passwordForgot.getVerificationCode().equals(verificationCode)) throw new InvalidResourceException("Código de verificação incorreto");
        if (passwordForgot.getExpiryDate().isBefore(LocalDateTime.now())) throw new CredentialsExpiredException("Código de verificação expirado, solicite um novo!");

        String passwordEncoded = new BCryptPasswordEncoder().encode(password);
        user.setPassword(passwordEncoded);

        this.userRepository.save(user);
        this.passwordForgotRepository.deleteById(passwordForgot.getId());
        log.info("Senha de usuário: {}, que esqueceu a senha alterada com sucesso", email);
    }
}
