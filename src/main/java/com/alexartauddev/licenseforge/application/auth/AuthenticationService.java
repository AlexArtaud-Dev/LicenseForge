package com.alexartauddev.licenseforge.application.auth;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthenticationResponse register(CreateUserRequest request) {
        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Verify company exists
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> CompanyNotFoundException.withId(request.getCompanyId()));

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .companyId(request.getCompanyId())
                .teamId(request.getTeamId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        UserDTO userDTO = userMapper.toDTO(savedUser);
        userDTO.setCompanyName(company.getName());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        UserDTO userDTO = userMapper.toDTO(user);

        // Enrich DTO with company name
        companyRepository.findById(user.getCompanyId()).ifPresent(company ->
                userDTO.setCompanyName(company.getName()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .build();
    }
}