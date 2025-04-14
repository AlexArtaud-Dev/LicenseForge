package com.alexartauddev.licenseforge.unit.auth.service;

import com.alexartauddev.licenseforge.application.auth.AuthenticationService;
import com.alexartauddev.licenseforge.application.jwt.service.JwtService;
import com.alexartauddev.licenseforge.application.user.mapper.UserMapper;
import com.alexartauddev.licenseforge.domain.company.entity.Company;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.user.DuplicateEmailException;
import com.alexartauddev.licenseforge.web.request.auth.AuthenticationRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.response.auth.AuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UUID userId;
    private UUID companyId;
    private User user;
    private UserDTO userDTO;
    private Company company;
    private UserDetails userDetails;
    private CreateUserRequest createUserRequest;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .passwordHash("hashedPassword")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .build();

        userDTO = UserDTO.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .companyName("Test Company")
                .build();

        createUserRequest = CreateUserRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .build();

        authRequest = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "hashedPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEVELOPER"))
        );
    }

    @Test
    void register_ValidRequest_ShouldRegisterUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        // Act
        AuthenticationResponse response = authenticationService.register(createUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(userDTO, response.getUser());

        verify(userRepository).existsByEmail("test@example.com");
        verify(companyRepository).findById(companyId);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(user);
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
    }

    @Test
    void register_DuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> authenticationService.register(createUserRequest));
        verify(userRepository).existsByEmail("test@example.com");
        verify(companyRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_NonExistingCompany_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> authenticationService.register(createUserRequest));
        verify(userRepository).existsByEmail("test@example.com");
        verify(companyRepository).findById(companyId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ValidCredentials_ShouldAuthenticateUser() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        // For AuthenticationManager.authenticate (which is not void), we use when() instead of doNothing()
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(userDTO, response.getUser());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toDTO(user);
        verify(companyRepository).findById(companyId);
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
    }

    @Test
    void authenticate_NonExistingUser_ShouldThrowException() {
        // Arrange
        // For AuthenticationManager.authenticate (which is not void), we use when() instead of doNothing()
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(authRequest));
        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(userRepository).findByEmail("test@example.com");
    }
}