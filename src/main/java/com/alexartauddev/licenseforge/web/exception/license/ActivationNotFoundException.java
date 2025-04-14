package com.alexartauddev.licenseforge.web.exception.license;

import java.util.UUID;

public class ActivationNotFoundException extends RuntimeException {
    public ActivationNotFoundException(String message) {
        super(message);
    }

    public static ActivationNotFoundException withId(UUID id) {
        return new ActivationNotFoundException("Activation not found with id: " + id);
    }
}