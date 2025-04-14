package com.alexartauddev.licenseforge.web.exception.team;


import java.util.UUID;

public class TeamPermissionNotFoundException extends RuntimeException {
    public TeamPermissionNotFoundException(String message) {
        super(message);
    }

    public static TeamPermissionNotFoundException withId(UUID id) {
        return new TeamPermissionNotFoundException("Team permission not found with id: " + id);
    }
}