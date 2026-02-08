package com.onboard.registration.application.exception;

import com.onboard.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/** Raised when a registration form cannot be found. */
public class RegistrationFormNotFoundException extends AbstractPlatformDomainRuleException {

  private static final String MESSAGE_CODE = "registration.form.not-found";

  /** Creates a not-found exception for the given form id. */
  public RegistrationFormNotFoundException(String formId) {
    super(MESSAGE_CODE, "Registration form " + formId + " is not found");
  }
}
