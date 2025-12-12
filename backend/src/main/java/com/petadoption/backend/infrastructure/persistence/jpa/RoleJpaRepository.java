package com.petadoption.backend.infrastructure.persistence.jpa;

import com.petadoption.backend.core.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}