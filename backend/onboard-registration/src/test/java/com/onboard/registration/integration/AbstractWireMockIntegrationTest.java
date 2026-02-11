package com.onboard.registration.integration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Shared WireMock server setup for integration tests that mock external APIs. */
abstract class AbstractWireMockIntegrationTest {

  private static final List<String> DEFAULT_WIREMOCK_BASE_URL_PROPERTIES =
      List.of(
          "wiremock.server.base-url",
          "integration.external.base-url",
          "clients.customer.base-url",
          "clients.product.base-url");

  @RegisterExtension
  static final WireMockExtension WIREMOCK =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  static void registerWireMockBaseUrlProperties(DynamicPropertyRegistry registry) {
    DEFAULT_WIREMOCK_BASE_URL_PROPERTIES.forEach(
        property -> registry.add(property, () -> WIREMOCK.getRuntimeInfo().getHttpBaseUrl()));
  }

  @BeforeEach
  void resetWireMock() {
    WIREMOCK.resetAll();
  }

  protected String wireMockBaseUrl() {
    return WIREMOCK.getRuntimeInfo().getHttpBaseUrl();
  }
}
