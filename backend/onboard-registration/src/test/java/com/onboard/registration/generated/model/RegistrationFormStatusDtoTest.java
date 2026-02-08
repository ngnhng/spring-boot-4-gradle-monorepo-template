package com.onboard.registration.generated.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RegistrationFormStatusDtoTest {

  @Test
  void fromValueParsesKnownStatusAndRejectsUnknownStatus() {
    assertThat(RegistrationFormStatusDto.fromValue("DRAFT"))
        .isEqualTo(RegistrationFormStatusDto.DRAFT);

    assertThatThrownBy(() -> RegistrationFormStatusDto.fromValue("UNKNOWN"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unexpected value");
  }
}
