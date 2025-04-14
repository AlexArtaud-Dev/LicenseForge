package com.alexartauddev.licenseforge.domain.license.valueobject;

import java.security.SecureRandom;

public class LicenseKey {
    private final String value;
    private static final SecureRandom random = new SecureRandom();

    private LicenseKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LicenseKey generate(String prefix) {
        StringBuilder key = new StringBuilder(prefix);

        // Add 4 groups of 4 alphanumeric characters
        for (int i = 0; i < 4; i++) {
            key.append("-");
            for (int j = 0; j < 4; j++) {
                key.append(randomAlphaNumeric());
            }
        }

        return new LicenseKey(key.toString());
    }

    private static char randomAlphaNumeric() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Removed similar looking characters
        return chars.charAt(random.nextInt(chars.length()));
    }
}