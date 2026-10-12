package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.util.AESCipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AESCipherServiceTest {

    private final AESCipherService aesCipherService = new AESCipherService();

    @Test
    @DisplayName("Given valid plain text and key, when encrypting, then return encrypted string")
    void givenValidPlainTextAndKey_whenEncrypt_thenReturnEncryptedString() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String secretKey = "1234567890123456";
        String expectedEncrypted = "encryptedData123";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given empty plain text and valid key, when encrypting, then return encrypted string")
    void givenEmptyPlainTextAndValidKey_whenEncrypt_thenReturnEncryptedString() throws Exception {
        // Arrange
        String plainText = "";
        String secretKey = "1234567890123456";
        String expectedEncrypted = "encryptedEmpty";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given null plain text and valid key, when encrypting, then delegate to AESCipher")
    void givenNullPlainTextAndValidKey_whenEncrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String plainText = null;
        String secretKey = "1234567890123456";
        String expectedEncrypted = "encryptedNull";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given valid plain text and null key, when encrypting, then delegate to AESCipher")
    void givenValidPlainTextAndNullKey_whenEncrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String plainText = "Hello";
        String secretKey = null;
        String expectedEncrypted = "encryptedNullKey";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given null plain text and null key, when encrypting, then delegate to AESCipher")
    void givenNullPlainTextAndNullKey_whenEncrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String plainText = null;
        String secretKey = null;
        String expectedEncrypted = "encryptedBothNull";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given valid plain text and key, when encrypting with exception in AESCipher, then throw exception")
    void givenValidPlainTextAndKey_whenEncryptWithExceptionInAESCipher_thenThrowException() {
        // Arrange
        String plainText = "Hello";
        String secretKey = "1234567890123456";
        Exception expectedException = new Exception("Encryption failed");

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenThrow(expectedException);

            // Act & Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                aesCipherService.encrypt(plainText, secretKey);
            });
            assertEquals(expectedException.getMessage(), thrown.getMessage());
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given valid encrypted text and key, when decrypting, then return decrypted string")
    void givenValidEncryptedTextAndKey_whenDecrypt_thenReturnDecryptedString() throws Exception {
        // Arrange
        String encryptedText = "encryptedData123";
        String secretKey = "1234567890123456";
        String expectedDecrypted = "Hello, World!";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenReturn(expectedDecrypted);

            // Act
            String result = aesCipherService.decrypt(encryptedText, secretKey);

            // Assert
            assertEquals(expectedDecrypted, result);
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given empty encrypted text and valid key, when decrypting, then return decrypted string")
    void givenEmptyEncryptedTextAndValidKey_whenDecrypt_thenReturnDecryptedString() throws Exception {
        // Arrange
        String encryptedText = "";
        String secretKey = "1234567890123456";
        String expectedDecrypted = "";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenReturn(expectedDecrypted);

            // Act
            String result = aesCipherService.decrypt(encryptedText, secretKey);

            // Assert
            assertEquals(expectedDecrypted, result);
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given null encrypted text and valid key, when decrypting, then delegate to AESCipher")
    void givenNullEncryptedTextAndValidKey_whenDecrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String encryptedText = null;
        String secretKey = "1234567890123456";
        String expectedDecrypted = "decryptedNull";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenReturn(expectedDecrypted);

            // Act
            String result = aesCipherService.decrypt(encryptedText, secretKey);

            // Assert
            assertEquals(expectedDecrypted, result);
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given valid encrypted text and null key, when decrypting, then delegate to AESCipher")
    void givenValidEncryptedTextAndNullKey_whenDecrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String encryptedText = "encryptedData";
        String secretKey = null;
        String expectedDecrypted = "decryptedNullKey";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenReturn(expectedDecrypted);

            // Act
            String result = aesCipherService.decrypt(encryptedText, secretKey);

            // Assert
            assertEquals(expectedDecrypted, result);
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given null encrypted text and null key, when decrypting, then delegate to AESCipher")
    void givenNullEncryptedTextAndNullKey_whenDecrypt_thenDelegateToAESCipher() throws Exception {
        // Arrange
        String encryptedText = null;
        String secretKey = null;
        String expectedDecrypted = "decryptedBothNull";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenReturn(expectedDecrypted);

            // Act
            String result = aesCipherService.decrypt(encryptedText, secretKey);

            // Assert
            assertEquals(expectedDecrypted, result);
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given valid encrypted text and key, when decrypting with exception in AESCipher, then throw exception")
    void givenValidEncryptedTextAndKey_whenDecryptWithExceptionInAESCipher_thenThrowException() {
        // Arrange
        String encryptedText = "encryptedData";
        String secretKey = "1234567890123456";
        Exception expectedException = new Exception("Decryption failed");

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.decrypt(encryptedText, secretKey)).thenThrow(expectedException);

            // Act & Assert
            Exception thrown = assertThrows(Exception.class, () -> {
                aesCipherService.decrypt(encryptedText, secretKey);
            });
            assertEquals(expectedException.getMessage(), thrown.getMessage());
            aesCipherMock.verify(() -> AESCipher.decrypt(encryptedText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given long plain text and valid key, when encrypting, then return encrypted string")
    void givenLongPlainTextAndValidKey_whenEncrypt_thenReturnEncryptedString() throws Exception {
        // Arrange
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("A");
        }
        String plainText = sb.toString();
        String secretKey = "1234567890123456";
        String expectedEncrypted = "longEncryptedData";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given special characters plain text and valid key, when encrypting, then return encrypted string")
    void givenSpecialCharactersPlainTextAndValidKey_whenEncrypt_thenReturnEncryptedString() throws Exception {
        // Arrange
        String plainText = "Special chars: @#$%^&*()!@#$%^&*()";
        String secretKey = "1234567890123456";
        String expectedEncrypted = "specialEncrypted";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }

    @Test
    @DisplayName("Given unicode plain text and valid key, when encrypting, then return encrypted string")
    void givenUnicodePlainTextAndValidKey_whenEncrypt_thenReturnEncryptedString() throws Exception {
        // Arrange
        String plainText = "Hello 世界! Привет! こんにちは!";
        String secretKey = "1234567890123456";
        String expectedEncrypted = "unicodeEncrypted";

        try (MockedStatic<AESCipher> aesCipherMock = mockStatic(AESCipher.class)) {
            aesCipherMock.when(() -> AESCipher.encrypt(plainText, secretKey)).thenReturn(expectedEncrypted);

            // Act
            String result = aesCipherService.encrypt(plainText, secretKey);

            // Assert
            assertEquals(expectedEncrypted, result);
            aesCipherMock.verify(() -> AESCipher.encrypt(plainText, secretKey), times(1));
        }
    }
}