package com.onboard.provider.config.database;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.replica", name = "jdbc-url")
public class ReplicaConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.replica")
  public DataSource readerDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }
}
