package com.onboard.registration.application.service;

import com.onboard.infrastructure.core.idempotency.Idempotent;
import com.onboard.registration.application.exception.RegistrationFormConflictException;
import com.onboard.registration.application.exception.RegistrationFormNotFoundException;
import com.onboard.registration.application.exception.RegistrationFormValidationException;
import com.onboard.registration.application.port.in.OnboardRegistrationWriteService;
import com.onboard.registration.application.port.out.RegistrationFormCommandPort;
import com.onboard.registration.domain.model.RegistrationForm;
import com.onboard.registration.domain.model.RegistrationFormStatus;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.node.ObjectNode;

/** Application service for registration form write use cases. */
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardRegistrationWriteServiceImpl implements OnboardRegistrationWriteService {

  private static final String DEFAULT_PRODUCT_CODE = "DEFAULT_ONBOARDING";
  private static final AtomicLong REFERENCE_SEQUENCE = new AtomicLong(1);

  private final RegistrationFormCommandPort registrationFormCommandPort;

  @Override
  public RegistrationForm createRegistrationForm(String productCode, ObjectNode formContent) {
    ObjectNode validatedFormContent = requireFormContent(formContent);
    Instant now = Instant.now();

    RegistrationForm form =
        new RegistrationForm(
            UUID.randomUUID().toString(),
            nextReferenceNo(now),
            normalizeProductCode(productCode),
            validatedFormContent,
            RegistrationFormStatus.DRAFT,
            null,
            now,
            now,
            null);

    return registrationFormCommandPort.save(form);
  }

  @Override
  public RegistrationForm updateRegistrationForm(String formId, ObjectNode formContent) {
    ObjectNode validatedFormContent = requireFormContent(formContent);
    RegistrationForm existingForm =
        registrationFormCommandPort
            .findById(formId)
            .orElseThrow(() -> new RegistrationFormNotFoundException(formId));

    try {
      RegistrationForm updatedForm = existingForm.updateDraft(validatedFormContent, Instant.now());
      return registrationFormCommandPort.save(updatedForm);
    } catch (IllegalStateException ex) {
      throw new RegistrationFormConflictException(ex.getMessage());
    }
  }

  @Override
  @Idempotent(expire = 60, timeUnit = TimeUnit.SECONDS, namespace = "registration.submit-form")
  public RegistrationForm submitRegistrationForm(String formId, String submissionNote) {
    RegistrationForm existingForm =
        registrationFormCommandPort
            .findById(formId)
            .orElseThrow(() -> new RegistrationFormNotFoundException(formId));

    try {
      RegistrationForm submittedForm = existingForm.submit(submissionNote, Instant.now());
      return registrationFormCommandPort.save(submittedForm);
    } catch (IllegalStateException ex) {
      throw new RegistrationFormConflictException(ex.getMessage());
    }
  }

  private static String normalizeProductCode(String productCode) {
    return productCode == null || productCode.isBlank() ? DEFAULT_PRODUCT_CODE : productCode;
  }

  private static ObjectNode requireFormContent(ObjectNode formContent) {
    if (formContent == null || formContent.isEmpty()) {
      throw new RegistrationFormValidationException("formContent must not be empty");
    }
    return formContent.deepCopy();
  }

  private static String nextReferenceNo(Instant now) {
    int year = OffsetDateTime.ofInstant(now, ZoneOffset.UTC).getYear();
    return "REG-" + year + "-" + String.format("%05d", REFERENCE_SEQUENCE.getAndIncrement());
  }
}
