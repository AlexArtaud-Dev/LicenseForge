package com.alexartauddev.licenseforge.web.exception.user;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Current password does not match");
    }
}