package com.onboard.provider.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Configures security filter chains based on enabled authentication mode. */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  /** Creates a stateless filter chain that injects mock authentication for local development. */
  @Bean
  @ConditionalOnProperty(name = "platform.security.mock.enabled", havingValue = "true")
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new MockAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /** Creates the OAuth2 resource server filter chain for authenticated API access. */
  @Bean
  @ConditionalOnProperty(name = "platform.security.oauth2.enabled", havingValue = "true")
  public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/v1/public/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

    return http.build();
  }

  /** Fallback filter chain when no explicit security mode is enabled. */
  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
}
