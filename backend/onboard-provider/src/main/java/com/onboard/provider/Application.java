package com.onboard.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Entry point for the provider Spring Boot application. */
@AutoConfigurationPackage(basePackages = "com.onboard")
@SpringBootApplication(scanBasePackages = "com.onboard")
@ConfigurationPropertiesScan(basePackages = "com.onboard")
public class Application {
  /** Bootstraps the Spring application context. */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
