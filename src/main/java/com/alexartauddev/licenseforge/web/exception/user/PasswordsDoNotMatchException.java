package com.alexartauddev.licenseforge.web.exception.user;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException() {
        super("New password and confirm password do not match");
    }
}