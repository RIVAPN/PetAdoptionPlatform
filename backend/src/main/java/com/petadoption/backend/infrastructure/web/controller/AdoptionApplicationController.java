package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.AdoptionApplication;
import com.petadoption.backend.core.domain.AdoptionApplicationService;
import com.petadoption.backend.infrastructure.web.dto.AdoptionApplicationResponse;
import com.petadoption.backend.infrastructure.web.dto.CreateAdoptionApplicationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoptions/applications")
public class AdoptionApplicationController {

    private final AdoptionApplicationService applicationService;

    public AdoptionApplicationController(AdoptionApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<AdoptionApplicationResponse> create(
            @RequestBody CreateAdoptionApplicationRequest request) {

        AdoptionApplication app = applicationService.createApplication(request);
        AdoptionApplicationResponse response = toResponse(app);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<AdoptionApplicationResponse> list(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long adopterUserId,
            @RequestParam(required = false) Long adopterOrgId) {

        List<AdoptionApplication> apps;

        if (petId != null) {
            apps = applicationService.listByPet(petId);
        } else if (adopterUserId != null) {
            apps = applicationService.listByAdopterUser(adopterUserId);
        } else if (adopterOrgId != null) {
            apps = applicationService.listByAdopterOrg(adopterOrgId);
        } else {
            // Sem filtros -> por enquanto, lista tudo
            apps = applicationService.listByPet(null); // não temos método sem filtro, então:
            // mais simples: chamar diretamente o repository
            throw new IllegalArgumentException("Informe petId, adopterUserId ou adopterOrgId.");
        }

        return apps.stream().map(this::toResponse).toList();
    }

    private AdoptionApplicationResponse toResponse(AdoptionApplication app) {
        String adopterType;
        Long adopterId;

        if (app.getAdopterUser() != null) {
            adopterType = "USER";
            adopterId = app.getAdopterUser().getId();
        } else if (app.getAdopterOrg() != null) {
            adopterType = "ORG";
            adopterId = app.getAdopterOrg().getId();
        } else {
            adopterType = "UNKNOWN";
            adopterId = null;
        }

        return new AdoptionApplicationResponse(
                app.getId(),
                app.getPet().getId(),
                adopterType,
                adopterId,
                app.getStatus(),
                app.getCompatibilityScore(),
                app.getHasBlockingFactor(),
                app.getCreatedAt(),
                app.getDecisionAt(),
                app.getRejectionReason()
        );
    }
}