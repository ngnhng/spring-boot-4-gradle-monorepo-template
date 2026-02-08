package com.onboard.provider.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Routes database operations to write or read datasource by transaction mode. */
public class ReplicaRoutingDataSource extends AbstractRoutingDataSource {

  /** Available datasource targets for routing decisions. */
  public enum DataSourceType {
    READ,
    WRITE
  }

  @Override
  protected Object determineCurrentLookupKey() {
    if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
      return DataSourceType.READ;
    }
    return DataSourceType.WRITE;
  }
}
