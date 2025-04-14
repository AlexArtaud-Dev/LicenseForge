package com.alexartauddev.licenseforge.web.exception.company;

import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String message) {
        super(message);
    }

    public static CompanyNotFoundException withId(UUID id) {
        return new CompanyNotFoundException("Company not found with id: " + id);
    }
}
