package com.alexartauddev.licenseforge.web.exception.license;

public class LicenseValidationException extends RuntimeException {
    public LicenseValidationException(String message) {
        super(message);
    }
}