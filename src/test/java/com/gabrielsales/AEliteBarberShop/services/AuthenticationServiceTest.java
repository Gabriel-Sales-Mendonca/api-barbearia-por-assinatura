package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.PendingUser;
import com.gabrielsales.AEliteBarberShop.repositories.PendingUserRepository;
import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PendingUserRepository pendingUserRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("Should throw exception when email not registered")
    void verifyEmail_ShouldThrowException_WhenEmailNotRegistered() {
        String email = "example@email.com";
        String verificationCode = "123";

        BDDMockito.given(pendingUserRepository.findByLogin(email)).willReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.authenticationService.verifyEmail(email, verificationCode));
    }

    @Test
    @DisplayName("Doesn't throw exception when everything is ok")
    void verifyEmail_ShouldNotThrowException_WhenEverythingIsOk() {
        String email = "example@email.com";
        String verificationCode = "123";

        PendingUser pendingUser = new PendingUser();
        pendingUser.setVerificationCode(verificationCode);
        pendingUser.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        BDDMockito.given(pendingUserRepository.findByLogin(email)).willReturn(Optional.of(pendingUser));

        Assertions.assertDoesNotThrow(() -> this.authenticationService.verifyEmail(email, verificationCode));
    }

    @Test
    @DisplayName("Should throw exception when email not registered")
    void resendCode_ShouldThrowException_WhenEmailNotRegistered() {
        String email = "example@email.com";

        BDDMockito.given(pendingUserRepository.findByLogin(email)).willReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.authenticationService.resendCode(email));
    }

    @Test
    @DisplayName("Doesn't throw exception when everything is ok")
    void resendCode_ShouldNotThrowException_WhenEverythingIsOk() {
        String email = "example@email.com";
        String verificationCode = "123";

        PendingUser pendingUser = new PendingUser();

        BDDMockito.given(pendingUserRepository.findByLogin(email)).willReturn(Optional.of(pendingUser));

        Assertions.assertDoesNotThrow(() -> this.authenticationService.resendCode(email));
    }

}