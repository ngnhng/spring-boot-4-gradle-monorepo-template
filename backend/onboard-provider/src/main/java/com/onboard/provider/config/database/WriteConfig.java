package com.onboard.provider.config.database;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers the primary datasource when primary JDBC properties are configured. */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.primary", name = "jdbc-url")
public class WriteConfig {

  /**
   * Creates the primary datasource from externalized configuration.
   *
   * @return primary datasource
   */
  @Bean
  @ConfigurationProperties("spring.datasource.primary")
  public DataSource writerDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }
}
