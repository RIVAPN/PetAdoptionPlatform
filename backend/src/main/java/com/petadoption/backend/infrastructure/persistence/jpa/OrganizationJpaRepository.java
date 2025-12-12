package com.petadoption.backend.infrastructure.persistence.jpa;

import com.petadoption.backend.core.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
}