package com.alexartauddev.licenseforge.web.exception;

import com.alexartauddev.licenseforge.web.exception.application.ApplicationNotFoundException;
import com.alexartauddev.licenseforge.web.exception.company.CompanyNotFoundException;
import com.alexartauddev.licenseforge.web.exception.company.DuplicateRealmIdException;
import com.alexartauddev.licenseforge.web.exception.license.ActivationNotFoundException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseActivationException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseNotFoundException;
import com.alexartauddev.licenseforge.web.exception.license.LicenseValidationException;
import com.alexartauddev.licenseforge.web.exception.realm.RealmNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.DuplicateTeamPermissionException;
import com.alexartauddev.licenseforge.web.exception.team.TeamNotFoundException;
import com.alexartauddev.licenseforge.web.exception.team.TeamPermissionNotFoundException;
import com.alexartauddev.licenseforge.web.exception.user.DuplicateEmailException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordMismatchException;
import com.alexartauddev.licenseforge.web.exception.user.PasswordsDoNotMatchException;
import com.alexartauddev.licenseforge.web.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(LicenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLicenseNotFoundException(LicenseNotFoundException ex) {
        log.error("License not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LicenseActivationException.class)
    public ResponseEntity<ErrorResponse> handleLicenseActivationException(LicenseActivationException ex) {
        log.error("License activation error: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LicenseValidationException.class)
    public ResponseEntity<ErrorResponse> handleLicenseValidationException(LicenseValidationException ex) {
        log.error("License validation error: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ActivationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleActivationNotFoundException(ActivationNotFoundException ex) {
        log.error("Activation not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    // Update in src/main/java/com/alexartauddev/licenseforge/presentation/exception/GlobalExceptionHandler.java

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        log.error("Duplicate email: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        log.error("Password mismatch: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordsDoNotMatchException(PasswordsDoNotMatchException ex) {
        log.error("Passwords do not match: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        log.error("Company not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateRealmIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRealmIdException(DuplicateRealmIdException ex) {
        log.error("Duplicate realm ID: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApplicationNotFoundException(ApplicationNotFoundException ex) {
        log.error("Application not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RealmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRealmNotFoundException(RealmNotFoundException ex) {
        log.error("Realm not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamNotFoundException(TeamNotFoundException ex) {
        log.error("Team not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeamPermissionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamPermissionNotFoundException(TeamPermissionNotFoundException ex) {
        log.error("Team permission not found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateTeamPermissionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTeamPermissionException(DuplicateTeamPermissionException ex) {
        log.error("Duplicate team permission: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, status);
    }
}