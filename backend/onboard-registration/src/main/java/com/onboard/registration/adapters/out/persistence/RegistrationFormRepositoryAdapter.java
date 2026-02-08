package com.onboard.registration.adapters.out.persistence;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.onboard.registration.application.port.out.RegistrationFormCommandPort;
import com.onboard.registration.application.port.out.RegistrationFormQueryPort;
import com.onboard.registration.domain.model.RegistrationForm;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Persistence adapter mapping registration forms between domain and JPA models. */
@Component
@RequiredArgsConstructor
public class RegistrationFormRepositoryAdapter
    implements RegistrationFormCommandPort, RegistrationFormQueryPort {

  private final RegistrationFormJpaRepository registrationFormJpaRepository;

  @Override
  public Optional<RegistrationForm> findById(String formId) {
    try {
      return registrationFormJpaRepository.findById(UUID.fromString(formId)).map(this::toDomain);
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }

  @Override
  public List<RegistrationForm> findAll() {
    return registrationFormJpaRepository.findAll().stream().map(this::toDomain).toList();
  }

  @Override
  public RegistrationForm save(RegistrationForm form) {
    RegistrationFormJpaEntity existingEntity = findEntityByStringId(form.id()).orElse(null);
    RegistrationFormJpaEntity savedEntity =
        registrationFormJpaRepository.save(toEntity(form, existingEntity));
    return toDomain(savedEntity);
  }

  private RegistrationFormJpaEntity toEntity(
      RegistrationForm form, RegistrationFormJpaEntity existingEntity) {
    RegistrationFormJpaEntity entity = new RegistrationFormJpaEntity();
    entity.setId(resolveEntityId(form.id(), existingEntity));
    entity.setCustomerId(
        existingEntity == null ? UUID.randomUUID() : existingEntity.getCustomerId());
    entity.setReferenceNo(form.referenceNo());
    entity.setProductCode(form.productCode());
    entity.setFormContentJson(form.formContent().deepCopy());
    entity.setStatus(form.status());
    entity.setSubmittedAt(form.submittedAt());
    entity.setCreatedAt(form.createdAt());
    entity.setUpdatedAt(form.updatedAt());
    entity.setSubmissionNote(form.submissionNote());
    return entity;
  }

  private RegistrationForm toDomain(RegistrationFormJpaEntity entity) {
    return new RegistrationForm(
        entity.getId().toString(),
        entity.getReferenceNo(),
        entity.getProductCode(),
        requiredFormContent(entity.getFormContentJson()),
        entity.getStatus(),
        entity.getSubmittedAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getSubmissionNote());
  }

  private Optional<RegistrationFormJpaEntity> findEntityByStringId(String id) {
    try {
      return registrationFormJpaRepository.findById(UUID.fromString(id));
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }

  private UUID resolveEntityId(String id, RegistrationFormJpaEntity existingEntity) {
    if (existingEntity != null) {
      return existingEntity.getId();
    }
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException ex) {
      return UUID.randomUUID();
    }
  }

  private static ObjectNode requiredFormContent(ObjectNode formContent) {
    return Objects.requireNonNull(formContent, "formContent must not be null").deepCopy();
  }
}
