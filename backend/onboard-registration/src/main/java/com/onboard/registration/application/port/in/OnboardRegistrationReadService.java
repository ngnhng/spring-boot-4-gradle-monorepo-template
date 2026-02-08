package com.onboard.registration.application.port.in;

import com.onboard.registration.domain.model.RegistrationForm;
import com.onboard.registration.domain.model.RegistrationFormPage;
import com.onboard.registration.domain.model.RegistrationFormStatus;

/** Input port for registration form read use cases. */
public interface OnboardRegistrationReadService {

  /** Returns form detail for a single registration form id. */
  RegistrationForm getRegistrationFormDetail(String formId);

  /** Returns a filtered, paged view of registration forms. */
  RegistrationFormPage listRegistrationForms(
      RegistrationFormStatus status, String keyword, Integer page, Integer size);
}
