package com.onboard.provider.config.database;

import com.onboard.infrastructure.core.persistence.PartitionAwareRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** Enables JPA support across the application context. */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "com.onboard",
    basePackageClasses = PartitionAwareRepositoryImpl.class)
public class JpaConfig {
  // Annotation-only configuration.
}
