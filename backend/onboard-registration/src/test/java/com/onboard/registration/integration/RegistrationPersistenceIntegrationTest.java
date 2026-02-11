package com.onboard.registration.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.onboard.registration.RegistrationIntegrationTestApplication;
import com.onboard.registration.adapters.out.persistence.RegistrationFormRepositoryAdapter;
import com.onboard.registration.domain.model.RegistrationForm;
import com.onboard.registration.domain.model.RegistrationFormStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

/** Verifies registration persistence against a real PostgreSQL database. */
@SpringBootTest(classes = RegistrationIntegrationTestApplication.class)
class RegistrationPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

  @Autowired private RegistrationFormRepositoryAdapter repositoryAdapter;

  @Test
  void savesAndReadsRegistrationFormFromPostgres() {
    Instant now = Instant.parse("2026-02-11T00:00:00Z");
    String formId = "fd4dcdb6-b2f6-4726-a17f-8ea363d2cd3e";

    RegistrationForm form =
        new RegistrationForm(
            formId,
            "REG-2026-00001",
            "SAVINGS",
            createFormContent(),
            RegistrationFormStatus.DRAFT,
            null,
            now,
            now,
            null);

    repositoryAdapter.save(form);

    RegistrationForm saved = repositoryAdapter.findById(formId).orElseThrow();

    assertThat(saved.id()).isEqualTo(formId);
    assertThat(saved.referenceNo()).isEqualTo("REG-2026-00001");
    assertThat(saved.formContent().get("firstName").stringValue()).isEqualTo("Taylor");
    assertThat(saved.status()).isEqualTo(RegistrationFormStatus.DRAFT);
  }

  private static ObjectNode createFormContent() {
    ObjectNode content = JsonNodeFactory.instance.objectNode();
    content.put("firstName", "Taylor");
    content.put("lastName", "Nguyen");
    content.put("email", "taylor@example.com");
    return content;
  }
}
