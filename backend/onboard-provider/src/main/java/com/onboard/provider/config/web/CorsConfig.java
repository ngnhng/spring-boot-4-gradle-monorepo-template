package com.onboard.provider.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configures cross-origin access for API endpoints. */
@Configuration
public class CorsConfig {

  /** Registers CORS mappings for local frontend origins. */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
      }
    };
  }
}
