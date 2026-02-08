package com.onboard.registration.application.port.in;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.onboard.registration.domain.model.RegistrationForm;

/** Input port for registration form write use cases. */
public interface OnboardRegistrationWriteService {

  /** Creates a new draft registration form. */
  RegistrationForm createRegistrationForm(String productCode, ObjectNode formContent);

  /** Updates an existing draft registration form. */
  RegistrationForm updateRegistrationForm(String formId, ObjectNode formContent);

  /** Submits a draft registration form. */
  RegistrationForm submitRegistrationForm(String formId, String submissionNote);
}
