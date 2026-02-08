package com.onboard.provider.config.database;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

/** Creates routing-aware datasource beans for write and replica traffic split. */
@Configuration
public class DataSourceConfig {

  /**
   * Builds the routing datasource that delegates by transaction read-only flag.
   *
   * @param writerDataSource primary datasource
   * @param readerDataSource replica datasource
   * @return routing datasource
   */
  @Bean
  public DataSource routingDataSource(
      @Qualifier("writerDataSource") DataSource writerDataSource,
      @Qualifier("readerDataSource") DataSource readerDataSource) {
    ReplicaRoutingDataSource routingDataSource = new ReplicaRoutingDataSource();

    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(ReplicaRoutingDataSource.DataSourceType.WRITE, writerDataSource);
    targetDataSources.put(ReplicaRoutingDataSource.DataSourceType.READ, readerDataSource);

    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(writerDataSource);

    return routingDataSource;
  }

  /**
   * Wraps routing datasource with lazy connection proxy for transaction-aware lookup.
   *
   * @param routingDataSource routing datasource bean
   * @return primary datasource proxy
   */
  @Primary
  @Bean
  public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }
}
