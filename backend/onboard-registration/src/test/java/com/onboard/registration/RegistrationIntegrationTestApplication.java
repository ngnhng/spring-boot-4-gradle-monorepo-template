package com.onboard.registration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/** Test-only bootstrap application for onboard-registration integration tests. */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.onboard.registration")
public class RegistrationIntegrationTestApplication {}
