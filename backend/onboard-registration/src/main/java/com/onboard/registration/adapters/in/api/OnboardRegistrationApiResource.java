package com.onboard.registration.adapters.in.api;

import com.onboard.registration.application.port.in.OnboardRegistrationReadService;
import com.onboard.registration.application.port.in.OnboardRegistrationWriteService;
import com.onboard.registration.domain.model.RegistrationForm;
import com.onboard.registration.domain.model.RegistrationFormPage;
import com.onboard.registration.domain.model.RegistrationFormStatus;
import com.onboard.registration.generated.api.RegistrationApiDelegate;
import com.onboard.registration.generated.model.CreateRegistrationFormRequestDto;
import com.onboard.registration.generated.model.RegistrationFormDto;
import com.onboard.registration.generated.model.RegistrationFormPageDto;
import com.onboard.registration.generated.model.RegistrationFormStatusDto;
import com.onboard.registration.generated.model.SubmitRegistrationFormRequestDto;
import com.onboard.registration.generated.model.UpdateRegistrationFormRequestDto;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/** REST adapter for registration form operations. */
@Component
@RequiredArgsConstructor
public class OnboardRegistrationApiResource implements RegistrationApiDelegate {

  private final OnboardRegistrationReadService onboardRegistrationReadService;
  private final OnboardRegistrationWriteService onboardRegistrationWriteService;
  private final ObjectMapper objectMapper;

  @Override
  public ResponseEntity<RegistrationFormDto> createRegistrationForm(
      CreateRegistrationFormRequestDto createRegistrationFormRequestDto) {
    RegistrationForm createdForm =
        onboardRegistrationWriteService.createRegistrationForm(
            createRegistrationFormRequestDto.getProductCode(),
            toObjectNode(createRegistrationFormRequestDto.getFormContent()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdForm));
  }

  @Override
  public ResponseEntity<RegistrationFormDto> getRegistrationFormDetail(String formId) {
    return ResponseEntity.ok(
        toDto(onboardRegistrationReadService.getRegistrationFormDetail(formId)));
  }

  @Override
  public ResponseEntity<RegistrationFormPageDto> listRegistrationForms(
      RegistrationFormStatusDto status, String keyword, Integer page, Integer size) {
    RegistrationFormPage formPage =
        onboardRegistrationReadService.listRegistrationForms(
            status == null ? null : RegistrationFormStatus.valueOf(status.getValue()),
            keyword,
            page,
            size);
    return ResponseEntity.ok(toDto(formPage));
  }

  @Override
  public ResponseEntity<RegistrationFormDto> submitRegistrationForm(
      String formId, SubmitRegistrationFormRequestDto submitRegistrationFormRequestDto) {
    RegistrationForm submittedForm =
        onboardRegistrationWriteService.submitRegistrationForm(
            formId,
            Optional.ofNullable(submitRegistrationFormRequestDto)
                .map(SubmitRegistrationFormRequestDto::getSubmissionNote)
                .orElse(null));
    return ResponseEntity.ok(toDto(submittedForm));
  }

  @Override
  public ResponseEntity<RegistrationFormDto> updateRegistrationForm(
      String formId, UpdateRegistrationFormRequestDto updateRegistrationFormRequestDto) {
    RegistrationForm updatedForm =
        onboardRegistrationWriteService.updateRegistrationForm(
            formId, toObjectNode(updateRegistrationFormRequestDto.getFormContent()));
    return ResponseEntity.ok(toDto(updatedForm));
  }

  private RegistrationFormDto toDto(RegistrationForm form) {
    return RegistrationFormDto.builder()
        .id(form.id())
        .referenceNo(form.referenceNo())
        .productCode(form.productCode())
        .formContent(toMap(form.formContent()))
        .status(toDtoStatus(form.status()))
        .submittedAt(toOffsetDateTime(form.submittedAt()))
        .createdAt(toOffsetDateTime(form.createdAt()))
        .updatedAt(toOffsetDateTime(form.updatedAt()))
        .build();
  }

  private RegistrationFormPageDto toDto(RegistrationFormPage formPage) {
    return RegistrationFormPageDto.builder()
        .items(formPage.items().stream().map(this::toDto).toList())
        .page(formPage.page())
        .size(formPage.size())
        .totalElements(formPage.totalElements())
        .totalPages(formPage.totalPages())
        .build();
  }

  private RegistrationFormStatusDto toDtoStatus(RegistrationFormStatus status) {
    return RegistrationFormStatusDto.fromValue(status.name());
  }

  private ObjectNode toObjectNode(Map<String, Object> input) {
    return input == null ? null : objectMapper.valueToTree(input);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> toMap(ObjectNode input) {
    return objectMapper.convertValue(input, Map.class);
  }

  private static OffsetDateTime toOffsetDateTime(Instant value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
  }
}
