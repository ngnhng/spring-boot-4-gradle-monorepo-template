package com.onboard.registration.domain.model;

import java.util.List;

/** Paged response model for registration forms. */
public record RegistrationFormPage(
    List<RegistrationForm> items, int page, int size, long totalElements, int totalPages) {}
