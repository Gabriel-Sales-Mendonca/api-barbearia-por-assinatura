package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.UserResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.mappers.UserMapper;
import com.gabrielsales.AEliteBarberShop.services.AuthenticationService;
import com.gabrielsales.AEliteBarberShop.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, UserMapper userMapper, AuthenticationService authenticationService) {
        this.userMapper = userMapper;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> findUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UserDetails user = (UserDetails) authentication.getPrincipal();
        UserResponseDTO userResponseDTO = this.userMapper.toDTO((User) user);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }
}
