package com.onboard.registration.application.exception;

import com.onboard.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/** Raised when a registration form operation violates current form state. */
public class RegistrationFormConflictException extends AbstractPlatformDomainRuleException {

  private static final String MESSAGE_CODE = "registration.form.conflict";

  /** Creates a conflict exception with the provided message. */
  public RegistrationFormConflictException(String message) {
    super(MESSAGE_CODE, message);
  }
}
