package com.onboard.provider.config.database;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Resolves the current auditor identifier from Spring Security authentication. */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .filter(authentication -> authentication.isAuthenticated())
        .map(authentication -> authentication.getName())
        .filter(name -> !name.isBlank())
        .filter(name -> !"anonymousUser".equals(name));
  }
}
