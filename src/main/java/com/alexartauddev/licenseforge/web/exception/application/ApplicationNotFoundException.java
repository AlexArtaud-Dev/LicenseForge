package com.alexartauddev.licenseforge.web.exception.application;

import java.util.UUID;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(String message) {
        super(message);
    }

    public static ApplicationNotFoundException withId(UUID id) {
        return new ApplicationNotFoundException("Application not found with id: " + id);
    }
}
