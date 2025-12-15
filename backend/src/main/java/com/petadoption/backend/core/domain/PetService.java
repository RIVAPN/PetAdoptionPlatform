package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
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

    /**
     * Cria um pet.
     *
     * Casos:
     * 1) request.ownerUserId != null:
     *      - exige que seja o próprio usuário autenticado
     *      - garante ROLE_TUTOR
     * 2) request.ownerOrgId != null:
     *      - usa a organização como dona
     * 3) nenhum owner informado:
     *      - usa o usuário autenticado como dono
     *      - garante ROLE_TUTOR
     *
     * Só é erro quando OS DOIS (ownerUserId e ownerOrgId) são informados ao mesmo tempo.
     */
    @Transactional
    public Pet createPet(CreatePetRequest request, String authenticatedEmail) {
        boolean hasUserOwner = request.getOwnerUserId() != null;
        boolean hasOrgOwner  = request.getOwnerOrgId() != null;

        // agora só dá erro quando os dois estão preenchidos
        if (hasUserOwner && hasOrgOwner) {
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

        // 1) Dono explícito: usuário
        if (hasUserOwner) {
            User owner = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Tutor usuário não encontrado"));

            // só permite cadastrar pet em nome do próprio usuário logado
            if (!owner.getEmail().equalsIgnoreCase(authenticatedEmail)) {
                throw new IllegalStateException("Você não pode cadastrar pets em nome de outro usuário.");
            }

            pet.setOwnerUser(owner);
            promoteToTutorIfNeeded(owner);

        // 2) Dono explícito: organização
        } else if (hasOrgOwner) {
            Organization org = organizationRepository.findById(request.getOwnerOrgId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada"));
            pet.setOwnerOrg(org);

        // 3) Nenhum dono informado -> usa o usuário autenticado como tutor
        } else {
            if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
                throw new IllegalStateException(
                        "Usuário autenticado é obrigatório para cadastrar pet sem dono explícito.");
            }

            User owner = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));

            pet.setOwnerUser(owner);
            promoteToTutorIfNeeded(owner);
        }

        return petRepository.save(pet);
    }

    /** Extrai a lógica de “garantir ROLE_TUTOR” para reaproveitar. */
    private void promoteToTutorIfNeeded(User owner) {
        roleRepository.findByName("ROLE_TUTOR").ifPresent(role -> {
            if (!owner.getRoles().contains(role)) {
                owner.addRole(role);
            }
        });
    }

    /**
     * Atualiza um pet existente.
     * Apenas o tutor (dono usuário) pode atualizar o próprio pet.
     * Por enquanto, não permitimos atualizar pets de organização.
     * Os campos de dono (ownerUserId / ownerOrgId) são ignorados na atualização.
     */
    @Transactional
    public Pet updatePet(Long petId, CreatePetRequest request, String authenticatedEmail) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado"));

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));

        if (pet.getOwnerUser() != null) {
            if (!pet.getOwnerUser().getId().equals(authenticatedUser.getId())) {
                throw new IllegalStateException("Você não tem permissão para alterar este pet.");
            }
        } else if (pet.getOwnerOrg() != null) {
            throw new IllegalStateException("A edição de pets de organizações ainda não está disponível.");
        }

        // Atualiza apenas os dados do pet — o dono não muda aqui
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setSex(request.getSex());
        pet.setSize(request.getSize());
        pet.setAgeYears(request.getAgeYears());
        pet.setHasSpecialNeeds(request.getHasSpecialNeeds());
        pet.setHasContinuousTreatment(request.getHasContinuousTreatment());
        pet.setHasChronicDisease(request.getHasChronicDisease());
        pet.setHealthNotes(request.getHealthNotes());
        pet.setGoodWithOtherAnimals(request.getGoodWithOtherAnimals());
        pet.setRequiresConstantCare(request.getRequiresConstantCare());

        if (request.getStatus() != null) {
            pet.setStatus(request.getStatus());
        }

        return petRepository.save(pet);
    }

    /**
     * Exclui um pet.
     * Apenas o tutor (dono usuário) pode excluir o próprio pet.
     * Pets de organização ainda não podem ser excluídos por esta rota.
     */
    @Transactional
    public void deletePet(Long petId, String authenticatedEmail) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado"));

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));

        if (pet.getOwnerUser() != null) {
            if (!pet.getOwnerUser().getId().equals(authenticatedUser.getId())) {
                throw new IllegalStateException("Você não tem permissão para excluir este pet.");
            }
        } else if (pet.getOwnerOrg() != null) {
            throw new IllegalStateException("A exclusão de pets de organizações ainda não está disponível.");
        }

        petRepository.delete(pet);
    }

    public Pet getById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado"));
    }

    public List<Pet> listByStatus(PetStatus status) {
        return petRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Pet> listPetsOfAuthenticatedUser(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário Auntenticado não encontrado."));

        return petRepository.findByOwnerUserId(user.getId());
    }
}