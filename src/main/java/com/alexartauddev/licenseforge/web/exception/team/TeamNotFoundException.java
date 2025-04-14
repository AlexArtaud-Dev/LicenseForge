package com.alexartauddev.licenseforge.web.exception.team;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(String message) {
        super(message);
    }

    public static TeamNotFoundException withId(UUID id) {
        return new TeamNotFoundException("Team not found with id: " + id);
    }
}