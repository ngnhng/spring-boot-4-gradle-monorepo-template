package com.onboard.registration.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.onboard.registration.RegistrationIntegrationTestApplication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

/** Verifies WireMock setup for external API integration tests. */
@SpringBootTest(classes = RegistrationIntegrationTestApplication.class)
class ExternalApiWireMockIntegrationTest extends AbstractPostgresIntegrationTest {

  @Autowired private Environment environment;

  @Test
  void servesStubbedExternalApiResponse() throws Exception {
    WIREMOCK.stubFor(
        get(urlEqualTo("/external/customers/123"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"customerId\":\"123\",\"status\":\"ACTIVE\"}")));

    HttpRequest request =
        HttpRequest.newBuilder(URI.create(wireMockBaseUrl() + "/external/customers/123"))
            .GET()
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"customerId\":\"123\"");
    assertThat(response.body()).contains("\"status\":\"ACTIVE\"");

    assertThat(environment.getProperty("wiremock.server.base-url")).isEqualTo(wireMockBaseUrl());
    assertThat(environment.getProperty("integration.external.base-url"))
        .isEqualTo(wireMockBaseUrl());
    assertThat(environment.getProperty("clients.customer.base-url")).isEqualTo(wireMockBaseUrl());
    assertThat(environment.getProperty("clients.product.base-url")).isEqualTo(wireMockBaseUrl());
  }
}
