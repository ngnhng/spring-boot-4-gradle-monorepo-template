package com.onboard.infrastructure.core.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/** JPA repository implementation that adds partition-aware predicates to ID lookups. */
public class PartitionAwareRepositoryImpl<T, IdT extends Serializable>
    extends SimpleJpaRepository<T, IdT> implements PartitionAwareRepository<T, IdT> {

  private final EntityManager entityManager;
  private final JpaEntityInformation<T, ?> entityInformation;

  /**
   * Creates a partition-aware repository implementation.
   *
   * @param entityInformation metadata for the managed entity
   * @param entityManager JPA entity manager used for criteria queries
   */
  public PartitionAwareRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
    this.entityManager = entityManager;
  }

  @Override
  public Optional<T> findById(IdT id, PartitionHint hint) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<T> query = cb.createQuery(getDomainClass());
    Root<T> root = query.from(getDomainClass());

    List<Predicate> predicates = new ArrayList<>();

    // 1. Always add the ID Predicate
    predicates.add(cb.equal(root.get(entityInformation.getIdAttribute()), id));

    // 2. Iterate over Partition Hints and add them as predicates
    for (PartitionHint.Criterion c : hint.getCriteria()) {
      switch (c.operator) {
        case EQUAL:
          predicates.add(cb.equal(root.get(c.column), c.value1));
          break;
        case BETWEEN:
          predicates.add(
              cb.between(root.get(c.column), (Comparable) c.value1, (Comparable) c.value2));
          break;
        case IN:
          predicates.add(root.get(c.column).in((List<?>) c.value1));
          break;
        case GREATER_THAN:
          predicates.add(cb.greaterThan(root.get(c.column), (Comparable) c.value1));
          break;
        case LESS_THAN:
          predicates.add(cb.lessThan(root.get(c.column), (Comparable) c.value1));
          break;
        default:
          throw new IllegalArgumentException("Unsupported partition operator: " + c.operator);
      }
    }

    // 3. Combine all with AND
    query.where(cb.and(predicates.toArray(new Predicate[0])));

    // 4. Execute partition-constrained query first
    Optional<T> partitionedResult =
        entityManager.createQuery(query).getResultList().stream().findFirst();
    if (partitionedResult.isPresent() || !hint.isAllowFullScan()) {
      return partitionedResult;
    }

    // 5. Optional fallback: ID-only lookup (may scan all partitions/table)
    return super.findById(id);
  }
}
