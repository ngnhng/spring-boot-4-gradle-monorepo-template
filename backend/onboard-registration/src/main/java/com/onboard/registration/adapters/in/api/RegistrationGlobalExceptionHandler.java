package com.onboard.registration.adapters.in.api;

import com.onboard.registration.application.exception.RegistrationFormConflictException;
import com.onboard.registration.application.exception.RegistrationFormNotFoundException;
import com.onboard.registration.application.exception.RegistrationFormValidationException;
import com.onboard.registration.generated.model.ErrorResponseDto;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Centralized exception-to-HTTP mapping for registration APIs. */
@RestControllerAdvice(basePackageClasses = OnboardRegistrationApiResource.class)
public class RegistrationGlobalExceptionHandler {

  @ExceptionHandler(RegistrationFormValidationException.class)
  public ResponseEntity<ErrorResponseDto> handleValidation(RegistrationFormValidationException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.geti18nMessageCode(), ex.getMessage());
  }

  @ExceptionHandler(RegistrationFormNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNotFound(RegistrationFormNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.geti18nMessageCode(), ex.getMessage());
  }

  @ExceptionHandler(RegistrationFormConflictException.class)
  public ResponseEntity<ErrorResponseDto> handleConflict(RegistrationFormConflictException ex) {
    return buildErrorResponse(HttpStatus.CONFLICT, ex.geti18nMessageCode(), ex.getMessage());
  }

  private static ResponseEntity<ErrorResponseDto> buildErrorResponse(
      HttpStatus status, String code, String message) {
    ErrorResponseDto errorResponse =
        ErrorResponseDto.builder()
            .code(code)
            .message(message)
            .details(Collections.emptyList())
            .build();
    return ResponseEntity.status(status).body(errorResponse);
  }
}
