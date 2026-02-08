package com.onboard.registration.adapters.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for registration form entities. */
public interface RegistrationFormJpaRepository
    extends JpaRepository<RegistrationFormJpaEntity, UUID> {}
