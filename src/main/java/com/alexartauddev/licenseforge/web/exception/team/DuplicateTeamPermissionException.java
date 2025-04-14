package com.alexartauddev.licenseforge.web.exception.team;


import java.util.UUID;

public class DuplicateTeamPermissionException extends RuntimeException {
    public DuplicateTeamPermissionException(UUID teamId, UUID appId) {
        super("Permission for team " + teamId + " and application " + appId + " already exists");
    }
}