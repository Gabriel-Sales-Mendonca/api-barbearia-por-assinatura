package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.ValidateVerificationSignatureTokenResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.*;
import com.gabrielsales.AEliteBarberShop.repositories.SignatureRepository;
import com.gabrielsales.AEliteBarberShop.repositories.VerificationSignatureTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationSignatureTokenServiceTest {

    @InjectMocks
    private VerificationSignatureTokenService verificationSignatureTokenService;

    @Mock
    private VerificationSignatureTokenRepository verificationSignatureTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private SignatureRepository signatureRepository;

    @Test
    @DisplayName("Should return a valid token string when generating verification token")
    void generateVerificationToken_ShouldReturnTokenString_WhenUserIsAuthenticated() {
        User user = new User("test@email.com", "password", "Name", "Lastname", UserRole.USER);
        user.setId(1L);
        when(userService.getTokenUser()).thenReturn(user);

        String result = verificationSignatureTokenService.generateVerificationToken();

        assertNotNull(result);
        assertEquals(64, result.length());
    }

    @Test
    @DisplayName("Should save token with correct data and delete old one when generating verification token")
    void generateVerificationToken_ShouldDeleteOldTokenAndSaveNewOne_WhenUserIsAuthenticated() {
        User user = new User("test@email.com", "password", "Name", "Lastname", UserRole.USER);
        user.setId(123L);
        when(userService.getTokenUser()).thenReturn(user);

        verificationSignatureTokenService.generateVerificationToken();

        verify(verificationSignatureTokenRepository, times(1)).deleteByUserId(123L);

        ArgumentCaptor<VerificationSignatureToken> tokenCaptor = ArgumentCaptor.forClass(VerificationSignatureToken.class);
        verify(verificationSignatureTokenRepository).save(tokenCaptor.capture());

        VerificationSignatureToken savedToken = tokenCaptor.getValue();
        assertEquals(123L, savedToken.getUserId());
        assertNotNull(savedToken.getVerificationToken());
        assertEquals(64, savedToken.getVerificationToken().length());
        assertTrue(savedToken.getExpireAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should validate token and return response DTO when token is valid")
    void validateVerificationToken_ShouldReturnResponse_WhenTokenIsValid() {
        String tokenStr = "valid-token";
        Long userId = 1L;
        VerificationSignatureToken token = new VerificationSignatureToken(tokenStr, userId, LocalDateTime.now().plusMinutes(5));
        
        User user = new User("test@email.com", "password", "John", "Doe", UserRole.USER);
        user.setId(userId);
        
        Plan plan = new Plan("Premium", "Desc", 50.0);
        Signature signature = new Signature(LocalDate.now(), LocalDate.now().plusDays(30), user, plan);
        
        when(verificationSignatureTokenRepository.findById(tokenStr)).thenReturn(Optional.of(token));
        when(signatureRepository.findByUserId(userId)).thenReturn(Optional.of(signature));

        ValidateVerificationSignatureTokenResponseDTO result = verificationSignatureTokenService.validateVerificationToken(tokenStr);

        assertNotNull(result);
        assertEquals("John", result.userName());
        assertEquals("Doe", result.userLastname());
        assertEquals("Premium", result.signatureResponseDTO().planResponseDTO().name());
        
        verify(verificationSignatureTokenRepository).delete(token);
    }

    @Test
    @DisplayName("Should throw exception when token is not found")
    void validateVerificationToken_ShouldThrowException_WhenTokenDoesNotExist() {
        String tokenStr = "invalid-token";
        when(verificationSignatureTokenRepository.findById(tokenStr)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> 
            verificationSignatureTokenService.validateVerificationToken(tokenStr)
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception and delete token when token is expired")
    void validateVerificationToken_ShouldThrowException_WhenTokenIsExpired() {
        String tokenStr = "expired-token";
        VerificationSignatureToken token = new VerificationSignatureToken(tokenStr, 1L, LocalDateTime.now().minusMinutes(1));
        
        when(verificationSignatureTokenRepository.findById(tokenStr)).thenReturn(Optional.of(token));

        Exception exception = assertThrows(RuntimeException.class, () -> 
            verificationSignatureTokenService.validateVerificationToken(tokenStr)
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());
        verify(verificationSignatureTokenRepository).delete(token);
    }

    @Test
    @DisplayName("Should throw exception when signature is not found for the user")
    void validateVerificationToken_ShouldThrowException_WhenSignatureNotFound() {
        String tokenStr = "valid-token";
        Long userId = 1L;
        VerificationSignatureToken token = new VerificationSignatureToken(tokenStr, userId, LocalDateTime.now().plusMinutes(5));
        
        when(verificationSignatureTokenRepository.findById(tokenStr)).thenReturn(Optional.of(token));
        when(signatureRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> 
            verificationSignatureTokenService.validateVerificationToken(tokenStr)
        );

        assertEquals("Assinatura não encontrada para este usuário", exception.getMessage());
    }

    @Test
    @DisplayName("Should call repository to delete tokens before current time")
    void deleteExpiredTokens_ShouldCallRepositoryDeleteAllByExpireAtBefore_WhenCalled() {
        verificationSignatureTokenService.deleteExpiredTokens();

        verify(verificationSignatureTokenRepository, times(1)).deleteAllByExpireAtBefore(any(LocalDateTime.class));
    }
}
