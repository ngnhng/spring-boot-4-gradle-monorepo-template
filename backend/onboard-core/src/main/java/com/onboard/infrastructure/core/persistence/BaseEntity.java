package com.onboard.infrastructure.core.persistence;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/** Abstract base class for persist-able entities. */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity<T> {

  @Id private @Nullable T id;
}
