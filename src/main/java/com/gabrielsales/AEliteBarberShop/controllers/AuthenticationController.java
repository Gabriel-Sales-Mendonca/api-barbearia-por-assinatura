package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.AuthenticationDTO;
import com.gabrielsales.AEliteBarberShop.dtos.RegisterDTO;
import com.gabrielsales.AEliteBarberShop.dtos.VerifyEmailDTO;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.mappers.AuthenticationMapper;
import com.gabrielsales.AEliteBarberShop.repositories.PendingUserRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.AuthenticationService;
import com.gabrielsales.AEliteBarberShop.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private final AuthenticationMapper authenticationMapper;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PendingUserRepository pendingUserRepository,
            TokenService tokenService,
            AuthenticationService authenticationService,
            AuthenticationMapper authenticationMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
        this.authenticationMapper = authenticationMapper;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User) auth.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .secure(true)
                .httpOnly(true)
                .sameSite("Lax")
                .domain("aelitebarbershop.com.br")
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
    @Transactional
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (this.userRepository.existsByLogin(data.login()) || this.pendingUserRepository.existsByLogin(data.login())) {
            log.info("Login já existe no banco de dados");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        this.authenticationService.register(this.authenticationMapper.toEntity(data));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify-email")
    @Transactional
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailDTO verifyEmailDTO) {
        this.authenticationService.verifyEmail(verifyEmailDTO.email(), verifyEmailDTO.verificationCode());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/resend-code")
    @Transactional
    public ResponseEntity<?> resendCode(@RequestBody Map<String, String> resendEmail) {
        this.authenticationService.resendCode(resendEmail.get("email"));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
