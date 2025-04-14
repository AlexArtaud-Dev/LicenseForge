package com.alexartauddev.licenseforge.application.email.service.impl;


import com.alexartauddev.licenseforge.application.email.service.EmailTemplateService;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Override
    public String generateCompanyAdminWelcomeEmail(String adminEmail, String adminName, String password, String companyName) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #3366cc; color: white; padding: 10px 20px; text-align: center; }");
        html.append(".content { padding: 20px; }");
        html.append(".credentials { background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-left: 4px solid #3366cc; }");
        html.append(".footer { text-align: center; padding: 10px; font-size: 0.8em; color: #777; border-top: 1px solid #eee; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>Welcome to LicenseForge!</h1>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");
        html.append("<p>Dear ").append(adminName).append(",</p>");
        html.append("<p>Your company account for <strong>").append(companyName).append("</strong> has been created successfully.</p>");
        html.append("<p>Below are your admin login credentials:</p>");

        // Credentials
        html.append("<div class='credentials'>");
        html.append("<p><strong>Email:</strong> ").append(adminEmail).append("</p>");
        html.append("<p><strong>Password:</strong> ").append(password).append("</p>");
        html.append("</div>");

        html.append("<p><strong>Please login at <a href='https://app.licenseforge.com'>https://app.licenseforge.com</a> and change your password immediately.</strong></p>");
        html.append("<p>With these credentials, you can manage your company's licenses, applications, teams, and users.</p>");
        html.append("<p>If you have any questions, please contact our support team at support@licenseforge.com.</p>");
        html.append("<p>Best regards,<br>The LicenseForge Team</p>");
        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>© 2025 LicenseForge. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    @Override
    public String generateCompanyNewUserWelcomeEmail(String userEmail, String userName, String password, String companyName) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #3366cc; color: white; padding: 10px 20px; text-align: center; }");
        html.append(".content { padding: 20px; }");
        html.append(".credentials { background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-left: 4px solid #3366cc; }");
        html.append(".footer { text-align: center; padding: 10px; font-size: 0.8em; color: #777; border-top: 1px solid #eee; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>Welcome to LicenseForge!</h1>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");
        html.append("<p>Dear ").append(userName).append(",</p>");
        html.append("<p>You have been successfully added to <strong>").append(companyName).append("</strong>.</p>");
        html.append("<p>Below are your login credentials:</p>");

        // Credentials
        html.append("<div class='credentials'>");
        html.append("<p><strong>Email:</strong> ").append(userEmail).append("</p>");
        html.append("<p><strong>Password:</strong> ").append(password).append("</p>");
        html.append("</div>");

        html.append("<p><strong>Please login at <a href='https://app.licenseforge.com'>https://app.licenseforge.com</a> and change your password immediately.</strong></p>");
        html.append("<p>With these credentials, you can login and start using our app.</p>");
        html.append("<p>If you have any questions, please contact our support team at support@licenseforge.com.</p>");
        html.append("<p>Best regards,<br>The LicenseForge Team</p>");
        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>© 2025 LicenseForge. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}
