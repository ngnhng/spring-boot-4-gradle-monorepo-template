package com.onboard.registration.adapters.out.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.onboard.infrastructure.core.persistence.AuditableBaseEntity;
import com.onboard.registration.domain.model.RegistrationFormStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;

/** JPA entity for persisted registration forms. */
@Entity
@Table(name = "registration_forms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationFormJpaEntity extends AuditableBaseEntity<UUID, String> {

  @Column(name = "customer_id", nullable = false, length = 64)
  private UUID customerId;

  @Column(name = "reference_no", nullable = false, length = 64)
  private String referenceNo;

  @Column(name = "product_code", nullable = false, length = 128)
  private String productCode;

  @Column(name = "form_content", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private @Nullable ObjectNode formContentJson;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private RegistrationFormStatus status;

  @Column(name = "submitted_at")
  private @Nullable Instant submittedAt;

  @Column(name = "submission_note", length = 1000)
  private @Nullable String submissionNote;

  @Version
  @Column(name = "version", nullable = false)
  private @Nullable Long version;
}
