package com.alexartauddev.licenseforge.application.email.service;

/**
 * Service for sending emails
 */
public interface EmailService {

    /**
     * Sends a simple email message
     *
     * @param to The recipient email address
     * @param subject The email subject
     * @param text The email content
     */
    void sendSimpleMessage(String to, String subject, String text);

    /**
     * Sends an HTML email message
     *
     * @param to The recipient email address
     * @param subject The email subject
     * @param htmlContent The HTML content
     */
    void sendHtmlMessage(String to, String subject, String htmlContent);
}