package com.onboard.registration.application.port.out;

import com.onboard.registration.domain.model.RegistrationForm;
import java.util.List;
import java.util.Optional;

/** Output port for registration form query-side retrieval operations. */
public interface RegistrationFormQueryPort {
  /** Finds a form by id. */
  Optional<RegistrationForm> findById(String formId);

  /** Returns all registration forms. */
  List<RegistrationForm> findAll();
}
