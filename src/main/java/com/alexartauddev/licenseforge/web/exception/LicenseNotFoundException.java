package com.alexartauddev.licenseforge.web.exception;

public class LicenseNotFoundException extends RuntimeException {

    public LicenseNotFoundException(String message) {
        super(message);
    }

    public static LicenseNotFoundException withId(String id) {
        return new LicenseNotFoundException("License not found with id: " + id);
    }

    public static LicenseNotFoundException withKey(String key) {
        return new LicenseNotFoundException("License not found with key: " + key);
    }
}