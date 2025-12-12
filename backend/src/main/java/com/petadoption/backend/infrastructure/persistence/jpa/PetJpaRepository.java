package com.petadoption.backend.infrastructure.persistence.jpa;

import com.petadoption.backend.core.domain.Pet;
import com.petadoption.backend.core.domain.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetJpaRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByStatus(PetStatus status);
}