package com.onboard.registration.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

class RegistrationFormTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  void equalFormsHaveEqualHashCodes() {
    Instant now = Instant.parse("2026-02-10T10:15:30Z");

    RegistrationForm a =
        new RegistrationForm(
            "RF-1",
            "REF-001",
            "PRD-LOAN",
            createFormContent("Alice", 30),
            RegistrationFormStatus.DRAFT,
            null,
            now,
            now,
            null);

    RegistrationForm b =
        new RegistrationForm(
            "RF-1",
            "REF-001",
            "PRD-LOAN",
            createFormContent("Alice", 30),
            RegistrationFormStatus.DRAFT,
            null,
            now,
            now,
            null);

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  private static ObjectNode createFormContent(String name, int age) {
    ObjectNode formContent = OBJECT_MAPPER.createObjectNode();
    formContent.put("name", name);
    formContent.put("age", age);
    return formContent;
  }
}
