package com.gabrielsales.AEliteBarberShop.controllers;

import com.gabrielsales.AEliteBarberShop.dtos.UserResponseDTO;
import com.gabrielsales.AEliteBarberShop.dtos.UserUpdateDTO;
import com.gabrielsales.AEliteBarberShop.dtos.UserUpdatePasswordDTO;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.mappers.UserMapper;
import com.gabrielsales.AEliteBarberShop.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> findUser() {
        User user = this.userService.getTokenUser();
        UserResponseDTO userResponseDTO = this.userMapper.toDTO(user);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@RequestBody UserUpdateDTO data) {
        User updatedUser = this.userService.update(data);
        UserResponseDTO userResponseDTO = this.userMapper.toDTO(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
    }

    @PatchMapping
    public ResponseEntity<String> updatePassword(@RequestBody UserUpdatePasswordDTO data) {
        this.userService.updatePassword(data);

        return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso!");
    }
}
