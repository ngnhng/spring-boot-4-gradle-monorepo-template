package com.onboard.registration.domain.model;

import tools.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

/** Registration form aggregate root. */
public record RegistrationForm(
    String id,
    String referenceNo,
    String productCode,
    ObjectNode formContent,
    RegistrationFormStatus status,
    Instant submittedAt,
    Instant createdAt,
    Instant updatedAt,
    String submissionNote) {

  /** Creates a registration form and clones mutable JSON content. */
  public RegistrationForm {
    formContent = Objects.requireNonNull(formContent, "formContent must not be null").deepCopy();
  }

  /** Returns an updated draft registration form. */
  public RegistrationForm updateDraft(ObjectNode updatedFormContent, Instant now) {
    requireDraft("update");
    return new RegistrationForm(
        id,
        referenceNo,
        productCode,
        updatedFormContent,
        status,
        submittedAt,
        createdAt,
        now,
        submissionNote);
  }

  /** Returns a submitted registration form. */
  public RegistrationForm submit(String note, Instant now) {
    requireDraft("submit");
    return new RegistrationForm(
        id,
        referenceNo,
        productCode,
        formContent,
        RegistrationFormStatus.SUBMITTED,
        now,
        createdAt,
        now,
        note);
  }

  /** Returns whether the form matches a keyword in reference number or JSON content. */
  public boolean containsKeyword(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return true;
    }
    String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
    return (referenceNo != null && referenceNo.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
        || formContent.toString().toLowerCase(Locale.ROOT).contains(normalizedKeyword);
  }

  /** Ensures current form status is draft before applying a state transition. */
  private void requireDraft(String action) {
    if (status != RegistrationFormStatus.DRAFT) {
      throw new IllegalStateException(
          "Registration form " + id + " cannot " + action + " when status is " + status);
    }
  }
}
