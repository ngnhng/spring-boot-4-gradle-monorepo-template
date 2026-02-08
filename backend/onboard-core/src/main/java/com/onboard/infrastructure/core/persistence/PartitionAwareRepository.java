package com.onboard.infrastructure.core.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/** Repository contract that applies partition hints to ID-based lookups. */
@NoRepositoryBean
public interface PartitionAwareRepository<T, IdT> extends JpaRepository<T, IdT> {
  /**
   * Finds an entity by ID, constrained by a time window to enable Partition Pruning.
   *
   * @param id The primary key
   * @param hint The time window hint
   * @return Optional entity
   */
  Optional<T> findById(IdT id, PartitionHint hint);
}
