package com.alexartauddev.licenseforge.unit.email.service;

import com.alexartauddev.licenseforge.application.email.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private String fromEmail = "test@licenseforge.com";
    private String toEmail = "recipient@example.com";
    private String subject = "Test Subject";
    private String text = "Test email content";
    private String htmlContent = "<html><body><h1>Test HTML Content</h1></body></html>";

    @BeforeEach
    void setUp() {
        // Set properties via reflection since they would normally be injected via @Value
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
    }

    @Test
    void sendSimpleMessage_EmailEnabled_ShouldSendEmail() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendSimpleMessage(toEmail, subject, text);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleMessage_EmailDisabled_ShouldNotSendEmail() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", false);

        // Act
        emailService.sendSimpleMessage(toEmail, subject, text);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleMessage_ExceptionThrown_ShouldHandleGracefully() {
        // Arrange
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act - should not throw exception
        emailService.sendSimpleMessage(toEmail, subject, text);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtmlMessage_EmailEnabled_ShouldSendHtmlEmail() throws MessagingException {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlMessage(toEmail, subject, htmlContent);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendHtmlMessage_EmailDisabled_ShouldNotSendEmail() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", false);

        // Act
        emailService.sendHtmlMessage(toEmail, subject, htmlContent);

        // Assert
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendHtmlMessage_ExceptionThrown_ShouldHandleGracefully() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("HTML mail error"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlMessage(toEmail, subject, htmlContent);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}