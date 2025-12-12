package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetJpaRepository petRepository;
    private final UserJpaRepository userRepository;
    private final OrganizationJpaRepository organizationRepository;

    public PetService(PetJpaRepository petRepository,
                      UserJpaRepository userRepository,
                      OrganizationJpaRepository organizationRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Pet createPet(CreatePetRequest request) {
        boolean hasUserOwner = request.getOwnerUserId() != null;
        boolean hasOrgOwner = request.getOwnerOrgId() != null;

        if (hasUserOwner == hasOrgOwner) {
            throw new IllegalArgumentException(
                    "Pet deve possuir exatamente um tipo de dono: usuário OU organização.");
        }

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setSex(request.getSex());
        pet.setSize(request.getSize());
        pet.setAgeYears(request.getAgeYears());

        PetStatus status = request.getStatus() != null ? request.getStatus() : PetStatus.AVAILABLE;
        pet.setStatus(status);

        pet.setHasSpecialNeeds(request.getHasSpecialNeeds());
        pet.setHasContinuousTreatment(request.getHasContinuousTreatment());
        pet.setHasChronicDisease(request.getHasChronicDisease());
        pet.setHealthNotes(request.getHealthNotes());
        pet.setGoodWithOtherAnimals(request.getGoodWithOtherAnimals());
        pet.setRequiresConstantCare(request.getRequiresConstantCare());

        if (hasUserOwner) {
            User owner = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Tutor usuário não encontrado"));
            pet.setOwnerUser(owner);
        } else {
            Organization org = organizationRepository.findById(request.getOwnerOrgId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));
            pet.setOwnerOrg(org);
        }

        return petRepository.save(pet);
    }

    public Pet getById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado"));
    }

    public List<Pet> listByStatus(PetStatus status) {
        return petRepository.findByStatus(status);
    }
}