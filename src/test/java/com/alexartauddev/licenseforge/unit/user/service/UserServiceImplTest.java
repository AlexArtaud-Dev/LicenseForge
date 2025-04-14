package com.alexartauddev.licenseforge.unit.user.service;

import com.alexartauddev.licenseforge.application.user.mapper.UserMapper;
import com.alexartauddev.licenseforge.application.user.service.PasswordService;
import com.alexartauddev.licenseforge.application.user.service.impl.UserServiceImpl;
import com.alexartauddev.licenseforge.domain.company.repository.CompanyRepository;
import com.alexartauddev.licenseforge.domain.team.repository.TeamRepository;
import com.alexartauddev.licenseforge.domain.user.entity.User;
import com.alexartauddev.licenseforge.domain.user.repository.UserRepository;
import com.alexartauddev.licenseforge.web.dto.user.UserDTO;
import com.alexartauddev.licenseforge.web.exception.user.DuplicateEmailException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordMismatchException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordsDoNotMatchException;
import com.alexartauddev.licenseforge.web.exception.user.UserNotFoundException;
import com.alexartauddev.licenseforge.web.request.user.ChangePasswordRequest;
import com.alexartauddev.licenseforge.web.request.user.CreateUserRequest;
import com.alexartauddev.licenseforge.web.request.user.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private UUID companyId;
    private UUID teamId;
    private User user;
    private UserDTO userDTO;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        companyId = UUID.randomUUID();
        teamId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .passwordHash("hashedPassword")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .teamId(teamId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDTO = UserDTO.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .teamId(teamId)
                .companyName("Test Company")
                .teamName("Test Team")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createUserRequest = CreateUserRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .role(User.Role.DEVELOPER)
                .companyId(companyId)
                .teamId(teamId)
                .build();
    }

    @Test
    void createUser_ValidRequest_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordService.hashPassword(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // These are used in the implementation but might not be needed for this specific test path
        // Use lenient to avoid "unnecessary stubbing" errors
        lenient().when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));
        lenient().when(teamRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));

        // Act
        UserDTO result = userService.createUser(createUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO, result);
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordService).hashPassword("password");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(createUserRequest));
        verify(userRepository).existsByEmail("test@example.com");
        verify(companyRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mock()));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mock()));

        // Act
        UserDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDTO(user);
        verify(companyRepository).findById(companyId);
        verify(teamRepository).findById(teamId);
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toDTO(any(User.class));
    }

    @Test
    void getUserByEmail_ExistingUser_ShouldReturnUser() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(mock()));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mock()));

        // Act
        UserDTO result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO, result);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toDTO(user);
        verify(companyRepository).findById(companyId);
        verify(teamRepository).findById(teamId);
    }

    @Test
    void getUserByEmail_NonExistingUser_ShouldThrowException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
        verify(userRepository).findByEmail(email);
        verify(userMapper, never()).toDTO(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("user2@example.com")
                .build();

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Use lenient for stubbing that might be called in enrichUserDTO
        lenient().when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));
        lenient().when(teamRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));

        // Act
        List<UserDTO> result = userService.getAllUsers(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper, times(2)).toDTO(any(User.class));
    }

    @Test
    void updateUser_ValidRequest_ShouldUpdateUser() {
        // Arrange
        UUID newTeamId = UUID.randomUUID();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .role(User.Role.MANAGER)
                .teamId(newTeamId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        lenient().when(teamRepository.findById(newTeamId)).thenReturn(Optional.of(mock()));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        // Use lenient for stubbing that might be called in enrichUserDTO
        lenient().when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));
        lenient().when(teamRepository.findById(any(UUID.class))).thenReturn(Optional.of(mock()));

        // Act
        UserDTO result = userService.updateUser(userId, request);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO, result);
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);

        // Verify that user properties were updated
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals(User.Role.MANAGER, user.getRole());
        assertEquals(newTeamId, user.getTeamId());
    }

    @Test
    void updateUser_DuplicateEmail_ShouldThrowException() {
        // Arrange
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.updateUser(userId, request));
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ValidRequest_ShouldChangePassword() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("currentPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword(request.getCurrentPassword(), user.getPasswordHash())).thenReturn(true);
        when(passwordService.hashPassword(request.getNewPassword())).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.changePassword(userId, request);

        // Assert
        verify(userRepository).findById(userId);
        // Verify that we check the current password against the EXISTING hash
        verify(passwordService).verifyPassword(request.getCurrentPassword(), "hashedPassword");
        verify(passwordService).hashPassword(request.getNewPassword());
        verify(userRepository).save(user);

        // Verify that the password was updated
        assertEquals("newHashedPassword", user.getPasswordHash());
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ShouldThrowException() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword(request.getCurrentPassword(), user.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThrows(PasswordMismatchException.class, () -> userService.changePassword(userId, request));
        verify(userRepository).findById(userId);
        verify(passwordService).verifyPassword(request.getCurrentPassword(), user.getPasswordHash());
        verify(passwordService, never()).hashPassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_PasswordsDoNotMatch_ShouldThrowException() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("currentPassword")
                .newPassword("newPassword")
                .confirmPassword("differentPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword(request.getCurrentPassword(), user.getPasswordHash())).thenReturn(true);

        // Act & Assert
        assertThrows(PasswordsDoNotMatchException.class, () -> userService.changePassword(userId, request));
        verify(userRepository).findById(userId);
        verify(passwordService).verifyPassword(request.getCurrentPassword(), user.getPasswordHash());
        verify(passwordService, never()).hashPassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingUser_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void existsByEmail_EmailExists_ShouldReturnTrue() {
        // Arrange
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_EmailDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String email = "nonexisting@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void countByCompanyId_ShouldReturnCount() {
        // Arrange
        when(userRepository.countByCompanyId(companyId)).thenReturn(5L);

        // Act
        long result = userService.countByCompanyId(companyId);

        // Assert
        assertEquals(5L, result);
        verify(userRepository).countByCompanyId(companyId);
    }

    @Test
    void countByTeamId_ShouldReturnCount() {
        // Arrange
        when(userRepository.countByTeamId(teamId)).thenReturn(3L);

        // Act
        long result = userService.countByTeamId(teamId);

        // Assert
        assertEquals(3L, result);
        verify(userRepository).countByTeamId(teamId);
    }
}