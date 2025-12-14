package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetJpaRepository petRepository;
    private final UserJpaRepository userRepository;
    private final OrganizationJpaRepository organizationRepository;
    private final RoleJpaRepository roleRepository;

    public PetService(PetJpaRepository petRepository,
                      UserJpaRepository userRepository,
                      OrganizationJpaRepository organizationRepository,
                      RoleJpaRepository roleRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Pet createPet(CreatePetRequest request, String authenticatedEmail) {
        boolean hasUserOwnerField = request.getOwnerUserId() != null;
        boolean hasOrgOwner = request.getOwnerOrgId() != null;

        // não permitimos informar usuário E organização ao mesmo tempo
        if (hasUserOwnerField && hasOrgOwner) {
            throw new IllegalArgumentException(
                    "Pet deve possuir no máximo um tipo de dono: usuário OU organização.");
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

        if (hasOrgOwner) {
            // dono é uma organização
            Organization org = organizationRepository.findById(request.getOwnerOrgId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));
            pet.setOwnerOrg(org);
            pet.setOwnerUser(null);
        } else {
            // dono é SEMPRE o usuário autenticado
            User owner = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));

            pet.setOwnerUser(owner);
            pet.setOwnerOrg(null);

            // promoção automática para ROLE_TUTOR, se ainda não tiver
            boolean alreadyTutor = owner.getRoles() != null &&
                    owner.getRoles().stream()
                            .anyMatch(r -> "ROLE_TUTOR".equals(r.getName()));

            if (!alreadyTutor) {
                roleRepository.findByName("ROLE_TUTOR").ifPresent(role -> {
                    owner.addRole(role);
                    userRepository.save(owner); // persiste as novas roles
                });
            }
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