package com.alexartauddev.licenseforge.web.exception.license;

import java.util.UUID;

public class LicenseNotFoundException extends RuntimeException {
    public LicenseNotFoundException(String message) {
        super(message);
    }

    public static LicenseNotFoundException withId(UUID id) {
        return new LicenseNotFoundException("License not found with id: " + id);
    }

    public static LicenseNotFoundException withKey(String licenseKey) {
        return new LicenseNotFoundException("License not found with key: " + licenseKey);
    }
}
