package com.alexartauddev.licenseforge.web.exception.company;

public class DuplicateRealmIdException extends RuntimeException {
    public DuplicateRealmIdException(String realmId) {
        super("Company with realmId '" + realmId + "' already exists");
    }
}