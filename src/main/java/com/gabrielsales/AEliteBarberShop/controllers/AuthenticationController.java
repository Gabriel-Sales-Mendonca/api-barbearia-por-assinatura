package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.AuthenticationDTO;
import com.gabrielsales.AEliteBarberShop.dtos.RegisterDTO;
import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.UserRole;
import com.gabrielsales.AEliteBarberShop.repositories.PendingUserRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.EmailService;
import com.gabrielsales.AEliteBarberShop.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final TokenService tokenService;
    private final EmailService emailService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, PendingUserRepository pendingUserRepository, TokenService tokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User) auth.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(604800)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity logout() {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout realizado com sucesso!");
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data) {
        if (this.userRepository.existsByLogin(data.login()) || this.pendingUserRepository.existsByLogin(data.login())) {
            log.info("Login já existe no banco de dados");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String verificationCode = PendingUser.generateVerificationCode();

        String passwordEncoded = new BCryptPasswordEncoder().encode(data.password());

        PendingUser newPendingUser = new PendingUser(
                data.login(),
                passwordEncoded,
                data.name(),
                data.lastname(),
                verificationCode,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );

        this.pendingUserRepository.save(newPendingUser);
        log.info("Usuário pendente criado com sucesso");

        this.emailService.sendVerificationCodeEmail(data.login(), data.name(), verificationCode);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
