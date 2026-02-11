package com.onboard.registration.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Shared Testcontainers PostgreSQL setup for integration tests. */
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractPostgresIntegrationTest extends AbstractWireMockIntegrationTest {

  @Container
  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:17-alpine")
          .withDatabaseName("onboard_registration_it")
          .withUsername("onboard")
          .withPassword("onboard");

  @DynamicPropertySource
  static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }
}
