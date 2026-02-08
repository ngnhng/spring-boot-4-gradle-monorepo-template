package com.onboard.registration.architecture;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class OnboardRegistrationOnionArchitectureTest {

  @Test
  void shouldFollowOnionArchitecture() {
    JavaClasses classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.onboard.registration");

    onionArchitecture()
        .domainModels(
            "com.onboard.registration.domain.model..", "com.onboard.registration.domain.vo..")
        .domainServices("com.onboard.registration.domain.service..")
        .applicationServices("com.onboard.registration.application..")
        .adapter("incoming api", "com.onboard.registration.adapters.in.api..")
        .adapter("outgoing persistence", "com.onboard.registration.adapters.out.persistence..")
        .check(classes);
  }
}
