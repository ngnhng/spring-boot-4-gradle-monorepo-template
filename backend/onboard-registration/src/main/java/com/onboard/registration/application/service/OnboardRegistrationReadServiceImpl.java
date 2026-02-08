package com.onboard.registration.application.service;

import com.onboard.registration.application.exception.RegistrationFormNotFoundException;
import com.onboard.registration.application.port.in.OnboardRegistrationReadService;
import com.onboard.registration.application.port.out.RegistrationFormQueryPort;
import com.onboard.registration.domain.model.RegistrationForm;
import com.onboard.registration.domain.model.RegistrationFormPage;
import com.onboard.registration.domain.model.RegistrationFormStatus;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application service for registration form read use cases. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnboardRegistrationReadServiceImpl implements OnboardRegistrationReadService {

  private final RegistrationFormQueryPort registrationFormQueryPort;

  @Override
  public RegistrationForm getRegistrationFormDetail(String formId) {
    return registrationFormQueryPort
        .findById(formId)
        .orElseThrow(() -> new RegistrationFormNotFoundException(formId));
  }

  @Override
  public RegistrationFormPage listRegistrationForms(
      RegistrationFormStatus status, String keyword, Integer page, Integer size) {
    int resolvedPage = Math.max(page == null ? 0 : page, 0);
    int resolvedSize = Math.max(size == null ? 20 : size, 1);

    List<RegistrationForm> filteredForms =
        registrationFormQueryPort.findAll().stream()
            .filter(form -> status == null || form.status() == status)
            .filter(form -> form.containsKeyword(keyword))
            .sorted(Comparator.comparing(RegistrationForm::updatedAt).reversed())
            .toList();

    long totalElements = filteredForms.size();
    int totalPages =
        totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) resolvedSize);

    int fromIndex = Math.min(resolvedPage * resolvedSize, filteredForms.size());
    int toIndex = Math.min(fromIndex + resolvedSize, filteredForms.size());

    return new RegistrationFormPage(
        filteredForms.subList(fromIndex, toIndex),
        resolvedPage,
        resolvedSize,
        totalElements,
        totalPages);
  }
}
