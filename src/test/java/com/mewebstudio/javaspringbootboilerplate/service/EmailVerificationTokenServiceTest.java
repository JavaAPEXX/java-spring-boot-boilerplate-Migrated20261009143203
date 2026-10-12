package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.EmailVerificationToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenServiceTest {

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private EmailVerificationTokenService emailVerificationTokenService;

    private static final Long EXPIRES_IN = 3600L; // 1 hour in seconds

    @BeforeEach
    void setUp() {
        // Re-instantiate the service with the mocked dependencies and a fixed expiration time
        emailVerificationTokenService = new EmailVerificationTokenService(
            emailVerificationTokenRepository,
            messageSourceService,
            EXPIRES_IN
        );
    }

    @Test
    @DisplayName("Given an expired token, when checking expiration, then return true")
    void givenExpiredToken_whenIsEmailVerificationTokenExpired_thenReturnTrue() {
        // Arrange
        EmailVerificationToken token = EmailVerificationToken.builder()
            .token("expired-token")
            .expirationDate(Date.from(Instant.now().minusSeconds(100)))
            .build();

        // Act
        boolean isExpired = emailVerificationTokenService.isEmailVerificationTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Given a valid non-expired token, when checking expiration, then return false")
    void givenValidNonExpiredToken_whenIsEmailVerificationTokenExpired_thenReturnFalse() {
        // Arrange
        EmailVerificationToken token = EmailVerificationToken.builder()
            .token("valid-token")
            .expirationDate(Date.from(Instant.now().plusSeconds(100)))
            .build();

        // Act
        boolean isExpired = emailVerificationTokenService.isEmailVerificationTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Given a token expiring exactly now, when checking expiration, then return true")
    void givenTokenExpiringExactlyNow_whenIsEmailVerificationTokenExpired_thenReturnTrue() {
        // Arrange
        // Date.from(Instant.now()) is strictly before new Date() due to nanosecond precision differences
        EmailVerificationToken token = EmailVerificationToken.builder()
            .token("boundary-token")
            .expirationDate(Date.from(Instant.now()))
            .build();

        // Act
        boolean isExpired = emailVerificationTokenService.isEmailVerificationTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Given a user without existing token, when creating token, then save new token")
    void givenUserWithoutExistingToken_whenCreate_thenReturnNewToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        
        when(emailVerificationTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        EmailVerificationToken savedToken = EmailVerificationToken.builder()
            .user(user)
            .token("generated-token")
            .expirationDate(Date.from(Instant.now().plusSeconds(EXPIRES_IN)))
            .build();
        
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(savedToken);

        // Act
        EmailVerificationToken result = emailVerificationTokenService.create(user);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(emailVerificationTokenRepository, times(1)).findByUserId(userId);
        verify(emailVerificationTokenRepository, times(1)).save(any(EmailVerificationToken.class));
    }

    @Test
    @DisplayName("Given a user with existing token, when creating token, then update existing token")
    void givenUserWithExistingToken_whenCreate_thenReturnUpdatedToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        
        EmailVerificationToken oldToken = EmailVerificationToken.builder()
            .user(user)
            .token("old-token")
            .expirationDate(Date.from(Instant.now().minusSeconds(100)))
            .build();
            
        when(emailVerificationTokenRepository.findByUserId(userId)).thenReturn(Optional.of(oldToken));
        
        EmailVerificationToken savedToken = EmailVerificationToken.builder()
            .user(user)
            .token("new-generated-token")
            .expirationDate(Date.from(Instant.now().plusSeconds(EXPIRES_IN)))
            .build();
            
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class))).thenReturn(savedToken);

        // Act
        EmailVerificationToken result = emailVerificationTokenService.create(user);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(emailVerificationTokenRepository, times(1)).findByUserId(userId);
        verify(emailVerificationTokenRepository, times(1)).save(any(EmailVerificationToken.class));
    }

    @Test
    @DisplayName("Given a valid token string, when getting user by token, then return user")
    void givenValidTokenString_whenGetUserByToken_thenReturnUser() {
        // Arrange
        String tokenString = "valid-token-string";
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        
        EmailVerificationToken token = EmailVerificationToken.builder()
            .token(tokenString)
            .user(user)
            .expirationDate(Date.from(Instant.now().plusSeconds(100)))
            .build();
            
        when(emailVerificationTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        // Act
        User result = emailVerificationTokenService.getUserByToken(tokenString);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(emailVerificationTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    @DisplayName("Given a non-existent token string, when getting user by token, then throw NotFoundException")
    void givenNonExistentTokenString_whenGetUserByToken_thenThrowNotFoundException() {
        // Arrange
        String tokenString = "non-existent-token";
        
        when(emailVerificationTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());
        when(messageSourceService.get("not_found_with_param", any(String[].class))).thenReturn("Token not found");
        when(messageSourceService.get("token")).thenReturn("token");

        // Act & Assert
        assertThrows(NotFoundException.class, () -> emailVerificationTokenService.getUserByToken(tokenString));
        verify(emailVerificationTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    @DisplayName("Given an expired token string, when getting user by token, then throw BadRequestException")
    void givenExpiredTokenString_whenGetUserByToken_thenThrowBadRequestException() {
        // Arrange
        String tokenString = "expired-token-string";
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        
        EmailVerificationToken token = EmailVerificationToken.builder()
            .token(tokenString)
            .user(user)
            .expirationDate(Date.from(Instant.now().minusSeconds(100)))
            .build();
            
        when(emailVerificationTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));
        when(messageSourceService.get("expired_with_param", any(String[].class))).thenReturn("Token expired");
        when(messageSourceService.get("token")).thenReturn("token");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> emailVerificationTokenService.getUserByToken(tokenString));
        verify(emailVerificationTokenRepository, times(1)).findByToken(tokenString);
    }

    @Test
    @DisplayName("Given a valid user ID, when deleting token by user ID, then delete token")
    void givenValidUserId_whenDeleteByUserId_thenDeleteToken() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        emailVerificationTokenService.deleteByUserId(userId);

        // Assert
        verify(emailVerificationTokenRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Given a null user ID, when deleting token by user ID, then delete token with null")
    void givenNullUserId_whenDeleteByUserId_thenDeleteTokenWithNull() {
        // Arrange
        UUID userId = null;

        // Act
        emailVerificationTokenService.deleteByUserId(userId);

        // Assert
        verify(emailVerificationTokenRepository, times(1)).deleteByUserId(null);
    }
}