package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.entities.UserRole;
import com.gabrielsales.AEliteBarberShop.entities.VerificationSignatureToken;
import com.gabrielsales.AEliteBarberShop.repositories.VerificationSignatureTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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
    @DisplayName("Should save token with correct data when generating verification token")
    void generateVerificationToken_ShouldSaveTokenToDatabase_WhenUserIsAuthenticated() {
        User user = new User("test@email.com", "password", "Name", "Lastname", UserRole.USER);
        user.setId(123L);
        when(userService.getTokenUser()).thenReturn(user);

        verificationSignatureTokenService.generateVerificationToken();

        ArgumentCaptor<VerificationSignatureToken> tokenCaptor = ArgumentCaptor.forClass(VerificationSignatureToken.class);
        verify(verificationSignatureTokenRepository).save(tokenCaptor.capture());

        VerificationSignatureToken savedToken = tokenCaptor.getValue();
        assertEquals(123L, savedToken.getUserId());
        assertNotNull(savedToken.getVerificationToken());
        assertEquals(64, savedToken.getVerificationToken().length());
        assertTrue(savedToken.getExpireAt().isAfter(LocalDateTime.now()));
        assertTrue(savedToken.getExpireAt().isBefore(LocalDateTime.now().plusMinutes(6)));
    }

    @Test
    @DisplayName("Should call repository to delete tokens before current time")
    void deleteExpiredTokens_ShouldCallRepositoryDeleteAllByExpireAtBefore_WhenCalled() {
        verificationSignatureTokenService.deleteExpiredTokens();

        verify(verificationSignatureTokenRepository, times(1)).deleteAllByExpireAtBefore(any(LocalDateTime.class));
    }
}
