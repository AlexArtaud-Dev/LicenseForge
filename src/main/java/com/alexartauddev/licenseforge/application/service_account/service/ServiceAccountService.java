package com.alexartauddev.licenseforge.application.service_account.service;


/**
 * Service for managing and initializing the system service account
 */
public interface ServiceAccountService {

    /**
     * Ensures that a service account exists in the system
     * Creates one if it doesn't exist already
     */
    void ensureServiceAccountExists();

    /**
     * Checks if the given user is a service account
     * @param email The email to check
     * @return true if the email belongs to a service account
     */
    boolean isServiceAccount(String email);
}