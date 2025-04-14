package com.alexartauddev.licenseforge.web.exception.realm;

import java.util.UUID;

public class RealmNotFoundException extends RuntimeException {
    public RealmNotFoundException(String message) {
        super(message);
    }

    public static RealmNotFoundException withId(UUID id) {
        return new RealmNotFoundException("Realm not found with id: " + id);
    }
}
