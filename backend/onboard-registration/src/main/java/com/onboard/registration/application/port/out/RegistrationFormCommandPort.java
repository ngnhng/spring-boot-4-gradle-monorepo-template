package com.onboard.registration.application.port.out;

import com.onboard.registration.domain.model.RegistrationForm;
import java.util.Optional;

/** Output port for registration form command-side persistence operations. */
public interface RegistrationFormCommandPort {
  /** Finds a form by id. */
  Optional<RegistrationForm> findById(String formId);

  /** Persists a registration form aggregate. */
  RegistrationForm save(RegistrationForm form);
}
