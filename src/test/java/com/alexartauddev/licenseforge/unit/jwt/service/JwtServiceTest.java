package com.alexartauddev.licenseforge.unit.jwt.service;

import com.alexartauddev.licenseforge.application.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long jwtExpiration = 86400000; // 1 day in milliseconds
    private long refreshExpiration = 604800000; // 7 days in milliseconds
    private String username = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Set required fields via reflection since they would normally be injected via @Value
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        when(userDetails.getUsername()).thenReturn(username);
    }

    @Test
    void extractUsername_ValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify the token can be validated
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_WithClaims_ShouldIncludeClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("companyId", "123456");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);

        // Extract and verify claims
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        String companyId = jwtService.extractClaim(token, claims -> claims.get("companyId", String.class));

        assertEquals("ADMIN", role);
        assertEquals("123456", companyId);
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidToken() {
        // Act
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Assert
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        assertTrue(jwtService.isTokenValid(refreshToken, userDetails));
    }

    @Test
    void isTokenValid_ValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_ShouldReturnFalse() throws Exception {
        // Arrange - Create a JWT with a very short expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1); // 1 millisecond
        String token = jwtService.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(100); // Wait more than 1 ms

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WrongUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("another@example.com");

        // Act
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldExtractSpecificClaim() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        Date expirationDate = jwtService.extractClaim(token, claims -> claims.getExpiration());

        // Assert
        assertNotNull(expirationDate);
        // The expiration date should be in the future
        assertTrue(expirationDate.after(new Date()));
    }
}