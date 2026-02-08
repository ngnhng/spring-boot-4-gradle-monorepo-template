package com.onboard.infrastructure.core.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Auditable base class for persistent entities. */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableBaseEntity<IdT, AuditorIdT> extends BaseEntity<IdT> {

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private @Nullable Instant createdAt;

  @CreatedBy
  @Column(name = "createdby_id")
  private @Nullable AuditorIdT createdBy;

  @LastModifiedDate
  @Column(nullable = false)
  private @Nullable Instant updatedAt;

  @LastModifiedBy
  @Column(name = "lastmodifiedby_id")
  private @Nullable AuditorIdT lastModifiedBy;
}
