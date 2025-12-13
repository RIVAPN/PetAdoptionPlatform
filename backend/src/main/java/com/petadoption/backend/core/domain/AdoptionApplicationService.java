package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.AdoptionApplicationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateAdoptionApplicationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdoptionApplicationService {

    private final AdoptionApplicationJpaRepository applicationRepository;
    private final PetJpaRepository petRepository;
    private final UserJpaRepository userRepository;
    private final OrganizationJpaRepository organizationRepository;

    public AdoptionApplicationService(AdoptionApplicationJpaRepository applicationRepository,
                                      PetJpaRepository petRepository,
                                      UserJpaRepository userRepository,
                                      OrganizationJpaRepository organizationRepository) {
        this.applicationRepository = applicationRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public AdoptionApplication createApplication(CreateAdoptionApplicationRequest request) {
        boolean hasUser = request.getAdopterUserId() != null;
        boolean hasOrg = request.getAdopterOrgId() != null;

        if (!hasUser && !hasOrg) {
            throw new IllegalArgumentException("É necessário informar adopterUserId ou adopterOrgId.");
        }
        if (hasUser && hasOrg) {
            throw new IllegalArgumentException("Informe apenas adopterUserId ou adopterOrgId, não ambos.");
        }

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado"));

        AdoptionApplication app = new AdoptionApplication();
        app.setPet(pet);
        app.setStatus(ApplicationStatus.PENDING);

        // Por enquanto, score e blockingFactor são placeholders
        app.setCompatibilityScore(0);
        app.setHasBlockingFactor(false);

        if (hasUser) {
            User adopter = userRepository.findById(request.getAdopterUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário adotante não encontrado"));
            app.setAdopterUser(adopter);
        } else {
            Organization org = organizationRepository.findById(request.getAdopterOrgId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização adotante não encontrada"));
            app.setAdopterOrg(org);
        }

        return applicationRepository.save(app);
    }

    public List<AdoptionApplication> listByPet(Long petId) {
        return applicationRepository.findByPet_Id(petId);
    }

    public List<AdoptionApplication> listByAdopterUser(Long adopterUserId) {
        return applicationRepository.findByAdopterUser_Id(adopterUserId);
    }

    public List<AdoptionApplication> listByAdopterOrg(Long adopterOrgId) {
        return applicationRepository.findByAdopterOrg_Id(adopterOrgId);
    }
}