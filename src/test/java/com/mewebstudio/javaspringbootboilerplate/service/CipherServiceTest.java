package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.exception.CipherException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CipherServiceTest {

    @Mock
    private AESCipherService aesCipherService;

    @InjectMocks
    private CipherService cipherService;

    private static final String APP_SECRET = "app-secret-key-256-bit";
    private static final String CUSTOM_SECRET = "custom-secret-key-256-bit";
    private static final String PLAIN_TEXT = "Hello, World!";
    private static final String ENCRYPTED_TEXT = "encrypted-data";
    private static final String DECRYPTED_TEXT = "Hello, World!";

    @Test
    @DisplayName("Given valid plain text and custom secret key, when encrypting with custom key, then return encrypted text")
    void givenValidPlainTextAndCustomSecretKey_whenEncryptWithCustomKey_thenReturnEncryptedText() {
        // Arrange
        when(aesCipherService.encrypt(PLAIN_TEXT, CUSTOM_SECRET)).thenReturn(ENCRYPTED_TEXT);

        // Act
        String result = cipherService.encrypt(PLAIN_TEXT, CUSTOM_SECRET);

        // Assert
        assertEquals(ENCRYPTED_TEXT, result);
        verify(aesCipherService, times(1)).encrypt(PLAIN_TEXT, CUSTOM_SECRET);
    }

    @Test
    @DisplayName("Given valid plain text, when encrypting with app secret, then return encrypted text")
    void givenValidPlainText_whenEncryptWithAppSecret_thenReturnEncryptedText() {
        // Arrange
        // Re-instantiate CipherService with app secret since @InjectMocks doesn't handle @Value fields well
        CipherService serviceWithSecret = new CipherService(APP_SECRET, aesCipherService);
        when(aesCipherService.encrypt(PLAIN_TEXT, APP_SECRET)).thenReturn(ENCRYPTED_TEXT);

        // Act
        String result = serviceWithSecret.encrypt(PLAIN_TEXT);

        // Assert
        assertEquals(ENCRYPTED_TEXT, result);
        verify(aesCipherService, times(1)).encrypt(PLAIN_TEXT, APP_SECRET);
    }

    @Test
    @DisplayName("Given valid encrypted text and custom secret key, when decrypting with custom key, then return decrypted text")
    void givenValidEncryptedTextAndCustomSecretKey_whenDecryptWithCustomKey_thenReturnDecryptedText() {
        // Arrange
        when(aesCipherService.decrypt(ENCRYPTED_TEXT, CUSTOM_SECRET)).thenReturn(DECRYPTED_TEXT);

        // Act
        String result = cipherService.decrypt(ENCRYPTED_TEXT, CUSTOM_SECRET);

        // Assert
        assertEquals(DECRYPTED_TEXT, result);
        verify(aesCipherService, times(1)).decrypt(ENCRYPTED_TEXT, CUSTOM_SECRET);
    }

    @Test
    @DisplayName("Given valid encrypted text, when decrypting with app secret, then return decrypted text")
    void givenValidEncryptedText_whenDecryptWithAppSecret_thenReturnDecryptedText() {
        // Arrange
        CipherService serviceWithSecret = new CipherService(APP_SECRET, aesCipherService);
        when(aesCipherService.decrypt(ENCRYPTED_TEXT, APP_SECRET)).thenReturn(DECRYPTED_TEXT);

        // Act
        String result = serviceWithSecret.decrypt(ENCRYPTED_TEXT);

        // Assert
        assertEquals(DECRYPTED_TEXT, result);
        verify(aesCipherService, times(1)).decrypt(ENCRYPTED_TEXT, APP_SECRET);
    }

    @Test
    @DisplayName("Given null plain text and custom secret key, when encrypting, then throw CipherException")
    void givenNullPlainTextAndCustomSecretKey_whenEncrypt_ThenThrowCipherException() {
        // Arrange
        when(aesCipherService.encrypt(null, CUSTOM_SECRET)).thenThrow(new RuntimeException("Null input error"));

        // Act & Assert
        assertThrows(CipherException.class, () -> cipherService