package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.JwtToken;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.JwtTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Test
    @DisplayName("Given existing token by userId and refreshToken, when findByUserIdAndRefreshToken, then return JwtToken")
    void givenExistingTokenByUserIdAndRefreshToken_whenFindByUserIdAndRefreshToken_thenReturnJwtToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String refreshToken = "valid-refresh-token";
        JwtToken expectedToken = new JwtToken();
        expectedToken.setUserId(userId);
        expectedToken.setRefreshToken(refreshToken);

        when(jwtTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken))
                .thenReturn(Optional.of(expectedToken));

        // Act
        JwtToken result = jwtTokenService.findByUserIdAndRefreshToken(userId, refreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtTokenRepository, times(1)).findByUserIdAndRefreshToken(userId, refreshToken);
    }

    @Test
    @DisplayName("Given non-existing token by userId and refreshToken, when findByUserIdAndRefreshToken, then throw NotFoundException")
    void givenNonExistingTokenByUserIdAndRefreshToken_whenFindByUserIdAndRefreshToken_thenReturnNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String refreshToken = "non-existent-refresh-token";

        when(jwtTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken))
                .thenReturn(Optional.empty());
        when(messageSourceService.get("token")).thenReturn("token");
        when(messageSourceService.get("not_found_with_param", new String[]{"token"}))
                .thenReturn("token not found");

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            jwtTokenService.findByUserIdAndRefreshToken(userId, refreshToken);
        });

        verify(jwtTokenRepository, times(1)).findByUserIdAndRefreshToken(userId, refreshToken);
        verify(messageSourceService, times(1)).get("token");
        verify(messageSourceService, times(1)).get("not_found_with_param", new String[]{"token"});
    }

    @Test
    @DisplayName("Given null userId, when findByUserIdAndRefreshToken, then throw NotFoundException if repository returns empty")
    void givenNullUserId_whenFindByUserIdAndRefreshToken_thenReturnNotFoundException() {
        // Arrange
        String refreshToken = "some-refresh-token";

        when(jwtTokenRepository.findByUserIdAndRefreshToken(null, refreshToken))
                .thenReturn(Optional.empty());
        when(messageSourceService.get("token")).thenReturn("token");
        when(messageSourceService.get("not_found_with_param", new String[]{"token"}))
                .thenReturn("token not found");

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            jwtTokenService.findByUserIdAndRefreshToken(null, refreshToken);
        });

        verify(jwtTokenRepository, times(1)).findByUserIdAndRefreshToken(null, refreshToken);
    }

    @Test
    @DisplayName("Given null refreshToken, when findByUserIdAndRefreshToken, then throw NotFoundException if repository returns empty")
    void givenNullRefreshToken_whenFindByUserIdAndRefreshToken_thenReturnNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(jwtTokenRepository.findByUserIdAndRefreshToken(userId, null))
                .thenReturn(Optional.empty());
        when(messageSourceService.get("token")).thenReturn("token");
        when(messageSourceService.get("not_found_with_param", new String[]{"token"}))
                .thenReturn("token not found");

        //