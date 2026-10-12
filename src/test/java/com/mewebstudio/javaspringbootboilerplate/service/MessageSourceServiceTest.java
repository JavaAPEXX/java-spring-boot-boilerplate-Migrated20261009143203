package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSourceServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageSourceService messageSourceService;

    @BeforeEach
    void setUp() {
        // Reset LocaleContextHolder to a known state before each test
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    @DisplayName("Given valid code, params, and locale, when get is called, then return translated message")
    void givenValidCodeParamsAndLocale_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "greeting";
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Hello, John";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given valid code and params, when get is called with default locale, then return translated message")
    void givenValidCodeAndParams_whenGetWithDefaultLocale_thenReturnTranslatedMessage() {
        // Arrange
        String code = "greeting";
        Object[] params = new Object[]{"John"};
        String expectedMessage = "Hello, John";
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given valid code and locale, when get is called without params, then return translated message")
    void givenValidCodeAndLocale_whenGetWithoutParams_thenReturnTranslatedMessage() {
        // Arrange
        String code = "welcome";
        Locale locale = Locale.FRENCH;
        String expectedMessage = "Bienvenue";

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(locale));
    }

    @Test
    @DisplayName("Given valid code, when get is called with default locale and no params, then return translated message")
    void givenValidCode_whenGetWithDefaultLocaleAndNoParams_thenReturnTranslatedMessage() {
        // Arrange
        String code = "welcome";
        String expectedMessage = "Welcome";
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(defaultLocale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given code not found in message source, when get is called, then return the code itself")
    void givenCodeNotFound_whenGet_thenReturnCodeItself() {
        // Arrange
        String code = "non.existent.key";
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale)))
                .thenThrow(new NoSuchMessageException(code));

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(code, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given code not found in message source with default locale, when get is called, then return the code itself")
    void givenCodeNotFoundWithDefaultLocale_whenGet_thenReturnCodeItself() {
        // Arrange
        String code = "non.existent.key";
        Object[] params = new Object[]{"John"};
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale)))
                .thenThrow(new NoSuchMessageException(code));

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(code, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given code not found in message source without params, when get is called, then return the code itself")
    void givenCodeNotFoundWithoutParams_whenGet_thenReturnCodeItself() {
        // Arrange
        String code = "non.existent.key";
        Locale locale = Locale.GERMAN;

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(locale)))
                .thenThrow(new NoSuchMessageException(code));

        // Act
        String result = messageSourceService.get(code, locale);

        // Assert
        assertEquals(code, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(locale));
    }

    @Test
    @DisplayName("Given code not found in message source with default locale and no params, when get is called, then return the code itself")
    void givenCodeNotFoundWithDefaultLocaleAndNoParams_whenGet_thenReturnCodeItself() {
        // Arrange
        String code = "non.existent.key";
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(defaultLocale)))
                .thenThrow(new NoSuchMessageException(code));

        // Act
        String result = messageSourceService.get(code);

        // Assert
        assertEquals(code, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given empty params array, when get is called, then return translated message")
    void givenEmptyParamsArray_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "simple.message";
        Object[] params = new Object[0];
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Simple message";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null params array, when get is called, then return translated message")
    void givenNullParamsArray_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "simple.message";
        Object[] params = null;
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Simple message";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null locale, when get is called, then return translated message")
    void givenNullLocale_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "simple.message";
        Object[] params = new Object[]{"John"};
        Locale locale = null;
        String expectedMessage = "Simple message";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null code, when get is called, then return null or handle gracefully")
    void givenNullCode_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null code with default locale, when get is called, then return null or handle gracefully")
    void givenNullCodeWithDefaultLocale_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = new Object[]{"John"};
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given null code without params, when get is called, then return null or handle gracefully")
    void givenNullCodeWithoutParams_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(locale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, locale);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(locale));
    }

    @Test
    @DisplayName("Given null code with default locale and no params, when get is called, then return null or handle gracefully")
    void givenNullCodeWithDefaultLocaleAndNoParams_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(defaultLocale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given multiple params, when get is called, then return translated message with all params")
    void givenMultipleParams_whenGet_thenReturnTranslatedMessageWithAllParams() {
        // Arrange
        String code = "greeting.with.name.and.age";
        Object[] params = new Object[]{"John", 30};
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "Hello, John, you are 30 years old";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given different locale, when get is called, then return translated message for that locale")
    void givenDifferentLocale_whenGet_thenReturnTranslatedMessageForThatLocale() {
        // Arrange
        String code = "greeting";
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.JAPANESE;
        String expectedMessage = "こんにちは、ジョン";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given message source throws NoSuchMessageException, when get is called, then return code and log warning")
    void givenMessageSourceThrowsNoSuchMessageException_whenGet_thenReturnCodeAndLogWarning() {
        // Arrange
        String code = "missing.translation";
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale)))
                .thenThrow(new NoSuchMessageException(code));

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(code, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given empty string code, when get is called, then return translated message or code")
    void givenEmptyStringCode_whenGet_thenReturnTranslatedMessageOrCode() {
        // Arrange
        String code = "";
        Object[] params = new Object[]{"John"};
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given empty string code with default locale, when get is called, then return translated message or code")
    void givenEmptyStringCodeWithDefaultLocale_whenGet_thenReturnTranslatedMessageOrCode() {
        // Arrange
        String code = "";
        Object[] params = new Object[]{"John"};
        Locale defaultLocale = LocaleContextHolder.getLocale();
        String expectedMessage = "";

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given empty string code without params, when get is called, then return translated message or code")
    void givenEmptyStringCodeWithoutParams_whenGet_thenReturnTranslatedMessageOrCode() {
        // Arrange
        String code = "";
        Locale locale = Locale.ENGLISH;
        String expectedMessage = "";

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(locale));
    }

    @Test
    @DisplayName("Given empty string code with default locale and no params, when get is called, then return translated message or code")
    void givenEmptyStringCodeWithDefaultLocaleAndNoParams_whenGet_thenReturnTranslatedMessageOrCode() {
        // Arrange
        String code = "";
        Locale defaultLocale = LocaleContextHolder.getLocale();
        String expectedMessage = "";

        when(messageSource.getMessage(eq(code), any(Object[].class), eq(defaultLocale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), any(Object[].class), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given null params with default locale, when get is called, then return translated message")
    void givenNullParamsWithDefaultLocale_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "simple.message";
        Object[] params = null;
        Locale defaultLocale = LocaleContextHolder.getLocale();
        String expectedMessage = "Simple message";

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given null locale with params, when get is called, then return translated message")
    void givenNullLocaleWithParams_whenGet_thenReturnTranslatedMessage() {
        // Arrange
        String code = "simple.message";
        Object[] params = new Object[]{"John"};
        Locale locale = null;
        String expectedMessage = "Simple message";

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(expectedMessage);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null code and null params, when get is called, then return null or handle gracefully")
    void givenNullCodeAndNullParams_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = null;
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null code and null params with default locale, when get is called, then return null or handle gracefully")
    void givenNullCodeAndNullParamsWithDefaultLocale_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = null;
        Locale defaultLocale = LocaleContextHolder.getLocale();

        when(messageSource.getMessage(eq(code), eq(params), eq(defaultLocale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(defaultLocale));
    }

    @Test
    @DisplayName("Given null code and null locale, when get is called, then return null or handle gracefully")
    void givenNullCodeAndNullLocale_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = new Object[]{"John"};
        Locale locale = null;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }

    @Test
    @DisplayName("Given null code, null params, and null locale, when get is called, then return null or handle gracefully")
    void givenNullCodeNullParamsAndNullLocale_whenGet_thenReturnNullOrHandleGracefully() {
        // Arrange
        String code = null;
        Object[] params = null;
        Locale locale = null;

        when(messageSource.getMessage(eq(code), eq(params), eq(locale))).thenReturn(null);

        // Act
        String result = messageSourceService.get(code, params, locale);

        // Assert
        assertEquals(null, result);
        verify(messageSource).getMessage(eq(code), eq(params), eq(locale));
    }
}