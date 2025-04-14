package com.alexartauddev.licenseforge.application.email.service;

/**
 * Service for handling email templates
 */
public interface EmailTemplateService {

    /**
     * Generates a welcome email for a new company admin
     *
     * @param adminEmail The admin email address
     * @param adminName The admin name
     * @param password The generated password
     * @param companyName The company name
     * @return The email content
     */
    String generateCompanyAdminWelcomeEmail(String adminEmail, String adminName, String password, String companyName);

    /**
     * Generates a welcome email for a new company admin
     *
     * @param userEmail The user email address
     * @param userName The username
     * @param password The generated password
     * @param companyName The company name
     * @return The email content
     */
    String generateCompanyNewUserWelcomeEmail(String userEmail, String userName, String password, String companyName);
}