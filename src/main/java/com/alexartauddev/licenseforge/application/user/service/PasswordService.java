package com.alexartauddev.licenseforge.application.user.service;


public interface PasswordService {
    String hashPassword(String plainPassword);

    boolean verifyPassword(String plainPassword, String hashedPassword);
}

