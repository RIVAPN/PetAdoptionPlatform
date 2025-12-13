package com.petadoption.backend.infrastructure.persistence.jpa;

import com.petadoption.backend.core.domain.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdoptionApplicationJpaRepository extends JpaRepository<AdoptionApplication, Long> {

    List<AdoptionApplication> findByPet_Id(Long petId);

    List<AdoptionApplication> findByAdopterUser_Id(Long adopterUserId);

    List<AdoptionApplication> findByAdopterOrg_Id(Long adopterOrgId);
}