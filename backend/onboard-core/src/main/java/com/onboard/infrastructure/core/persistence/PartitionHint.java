package com.onboard.infrastructure.core.persistence;

import java.util.ArrayList;
import java.util.List;

/** Partition pruning hint builder for repository lookups on PostgreSQL 18. */
public class PartitionHint {
  private final List<Criterion> criteria = new ArrayList<>();
  private boolean allowFullScan;

  // Enum to handle different Postgres Partition strategies
  enum Operator {
    EQUAL,
    BETWEEN,
    GREATER_THAN,
    LESS_THAN,
    IN
  }

  static class Criterion {
    String column;
    Operator operator;
    Object value1;
    Object value2; // For BETWEEN

    Criterion(String col, Operator op, Object v1, Object v2) {
      this.column = col;
      this.operator = op;
      this.value1 = v1;
      this.value2 = v2;
    }
  }

  // --- FACTORY METHODS ---

  /**
   * Creates an empty hint instance.
   *
   * @return empty hint
   */
  public static PartitionHint none() {
    return new PartitionHint();
  }

  /**
   * Creates a mutable hint builder.
   *
   * @return new hint builder
   */
  public static PartitionHint builder() {
    return new PartitionHint();
  }

  // 1. For LIST and HASH Partitioning (Exact Match)
  // Example: region = 'US' or tenant_id = 500
  /**
   * Adds an equality predicate.
   *
   * @param column partitioned column name
   * @param value expected value
   * @return current hint for chaining
   */
  public PartitionHint exact(String column, Object value) {
    criteria.add(new Criterion(column, Operator.EQUAL, value, null));
    return this;
  }

  // 2. For RANGE Partitioning (Time or numeric ranges)
  // Example: created_at BETWEEN x AND y
  /**
   * Adds a range predicate.
   *
   * @param column partitioned column name
   * @param start range start
   * @param end range end
   * @return current hint for chaining
   */
  public PartitionHint between(String column, Comparable<?> start, Comparable<?> end) {
    criteria.add(new Criterion(column, Operator.BETWEEN, start, end));
    return this;
  }

  // 3. For List Partitioning (Subset)
  // Example: status IN ('OPEN', 'PENDING')
  /**
   * Adds an IN predicate.
   *
   * @param column partitioned column name
   * @param values accepted values
   * @return current hint for chaining
   */
  public PartitionHint in(String column, List<?> values) {
    criteria.add(new Criterion(column, Operator.IN, values, null));
    return this;
  }

  /**
   * Returns all configured partition criteria.
   *
   * @return configured criteria
   */
  public List<Criterion> getCriteria() {
    return criteria;
  }

  /**
   * Enables fallback to a non-partition-constrained ID lookup when no partitioned match is found.
   *
   * @return current hint for chaining
   */
  public PartitionHint allowFullScan() {
    this.allowFullScan = true;
    return this;
  }

  /**
   * Returns whether full-scan fallback is enabled.
   *
   * @return true if full-scan fallback is enabled
   */
  public boolean isAllowFullScan() {
    return allowFullScan;
  }
}
