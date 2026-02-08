package com.onboard.provider.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicaRoutingDataSource extends AbstractRoutingDataSource {

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
