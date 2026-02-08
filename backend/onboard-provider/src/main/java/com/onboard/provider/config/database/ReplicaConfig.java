package com.onboard.provider.config.database;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers the replica datasource when replica JDBC properties are configured. */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.replica", name = "jdbc-url")
public class ReplicaConfig {

  /**
   * Creates the replica datasource from externalized configuration.
   *
   * @return replica datasource
   */
  @Bean
  @ConfigurationProperties("spring.datasource.replica")
  public DataSource readerDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }
}
