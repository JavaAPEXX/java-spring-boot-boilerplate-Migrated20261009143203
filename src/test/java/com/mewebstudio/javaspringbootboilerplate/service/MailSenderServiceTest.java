package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.EmailVerificationToken;
import com.mewebstudio.javaspringbootboilerplate.entity.PasswordResetToken;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private MailSenderService mailSenderService;

    private static final String APP_NAME = "TestApp";
    private static final String APP_URL = "https://testapp.com";
    private static final String FRONTEND_URL = "https://frontend.testapp.com";
    private static final String SENDER_ADDRESS = "sender@testapp.com";

    @BeforeEach
    void setUp() {
        // Re-initialize the service with specific configuration values since @InjectMocks 
        // might not easily handle @Value constructor parameters without additional setup.
        mailSenderService = new MailSenderService(
            APP_NAME,
            APP_URL,
            FRONTEND_URL,
            SENDER_ADDRESS,
            messageSourceService,
            mailSender,
            templateEngine
        );
    }

    @Test
    @DisplayName("Given valid user with email verification token, when sending verification email, then email is sent successfully")
    void givenValidUserWithEmailVerificationToken_whenSendUserEmailVerification_thenEmailSentSuccessfully() throws Exception {
        // Arrange
        User user = createMockUser("John", "Doe", "john.doe@example.com");
        EmailVerificationToken token = mock(EmailVerificationToken.class);
        when(token.getToken()).thenReturn("valid-token-123");
        when(user.getEmailVerificationToken()).thenReturn(token);

        when(messageSourceService.get("email_verification")).thenReturn("Verify your email");
        when(templateEngine.process(anyString(), any())).thenReturn("<html>Verification Email</html>");
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> mailSenderService.sendUserEmailVerification(user));

        // Assert
        verify(messageSourceService, times(1)).get("email_verification");
        verify(templateEngine, times(1)).process(eq("mail/user-email-verification"), any());
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Given valid user with password reset token, when sending password reset email, then email is sent successfully")
    void givenValidUserWithPasswordResetToken_whenSendUserPasswordReset_thenEmailSentSuccessfully() throws Exception {
        // Arrange
        User user = createMockUser("Jane", "Smith", "jane.smith@example.com");
        PasswordResetToken token = mock(PasswordResetToken.class);
        when(token.getToken()).thenReturn("reset-token-456");
        when(user.getPasswordResetToken()).thenReturn(token);

        when(messageSourceService.get("password_reset")).thenReturn("Reset your password");
        when(templateEngine.process