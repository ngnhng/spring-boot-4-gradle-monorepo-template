package com.onboard.registration.application.exception;

import com.onboard.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/** Raised when registration form data fails validation. */
public class RegistrationFormValidationException extends AbstractPlatformDomainRuleException {

  private static final String MESSAGE_CODE = "registration.form.validation";

  /** Creates a validation exception with the provided message. */
  public RegistrationFormValidationException(String message) {
    super(MESSAGE_CODE, message);
  }
}
