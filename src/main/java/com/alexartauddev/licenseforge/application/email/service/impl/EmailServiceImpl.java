package com.alexartauddev.licenseforge.application.email.service.impl;

import com.alexartauddev.licenseforge.application.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${licenseforge.email.enabled:false}")
    private boolean emailEnabled;

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        if (!emailEnabled) {
            logEmailContent(to, subject, text);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Fallback to logging
            logEmailContent(to, subject, text);
        }
    }

    @Override
    @Async
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        if (!emailEnabled) {
            logEmailContent(to, subject, htmlContent);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    private void logEmailContent(String to, String subject, String content) {
        log.info("======================================================");
        log.info("EMAIL WOULD BE SENT TO: {}", to);
        log.info("SUBJECT: {}", subject);
        log.info("CONTENT:");
        log.info("{}", content);
        log.info("======================================================");
    }
}