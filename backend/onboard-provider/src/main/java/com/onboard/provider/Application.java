package com.onboard.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the provider Spring Boot application. */
@SpringBootApplication
public class Application {
  /** Bootstraps the Spring application context. */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
